import redis.asyncio as aioredis
from loguru import logger
from sqlalchemy.ext.asyncio import AsyncSession

from app.database.models import Post, Reaction
from app.ml.embeddings import embedding_model
from app.ml.recommender import recommender
from app.models.post import PostResponse, PostCreate
from app.models.reaction import ReactionCreate
from app.models.user import RecommendationRequest, RecommendationResponse
from config import settings


class RecommendationService:

    def __init__(self):
        self.recommender = recommender
        self.embedding_model = embedding_model
        self.redis_client: aioredis.Redis | None = None

    async def init_redis(self):
        try:
            redis_url = f"redis://:{settings.REDIS_PASSWORD}@{settings.REDIS_HOST}:{settings.REDIS_PORT}/{settings.REDIS_DB}"
            self.redis_client = await aioredis.from_url(
                redis_url,
                encoding="utf-8",
                decode_responses=False
            )
        except Exception as e:
            logger.error(f"Failed to connect to Redis: {e}")
            self.redis_client = None

    async def close_redis(self):
        if self.redis_client:
            await self.redis_client.close()

    async def get_recommendations(
            self,
            request: RecommendationRequest,
            session: AsyncSession
    ) -> RecommendationResponse:
        recommended_posts = await self.recommender.get_recommendations(
            user_id=request.user_id,
            session=session,
            limit=request.limit,
            exclude_author_posts=request.exclude_author_posts,
            redis_client=self.redis_client
        )

        post_responses = [
            PostResponse(
                id=p.id,
                authorId=p.author_id,
                createdAt=p.created_at,
            )
            for p in recommended_posts
        ]

        return RecommendationResponse(
            user_id=request.user_id,
            recommendations=post_responses,
            total_count=len(post_responses)
        )

    async def create_post(self, post_data: PostCreate, session: AsyncSession) -> PostResponse:
        embedding = None
        if post_data.text:
            embedding_array = self.embedding_model.encode(post_data.text)
            embedding = embedding_array[0] if embedding_array.ndim == 2 else embedding_array

        post = Post(
            id=post_data.id,
            author_id=post_data.author_id,
            created_at=post_data.created_at,
            embedding=embedding
        )

        session.add(post)
        await session.commit()
        await session.refresh(post)

        return PostResponse(
            id=post.id,
            authorId=post.author_id,
            createdAt=post.created_at,
        )

    async def create_reaction(
            self,
            reaction_data: ReactionCreate,
            session: AsyncSession
    ):
        reaction = Reaction(
            id=reaction_data.id,
            target_id=reaction_data.target_id,
            author_id=reaction_data.author_id,
            type=reaction_data.type.value,
            created_at=reaction_data.created_at
        )

        session.add(reaction)
        await session.commit()

        await self.recommender.invalidate_user_preference(
            reaction_data.author_id,
            session,
            self.redis_client
        )

    async def invalidate_preference_redis(self, user_id: int):
        if not self.redis_client:
            return
        try:
            cache_key = f"preference:{user_id}"
            await self.redis_client.delete(cache_key)
        except Exception as e:
            logger.error(f"Error invalidating preference in Redis: {e}")


recommendation_service = RecommendationService()