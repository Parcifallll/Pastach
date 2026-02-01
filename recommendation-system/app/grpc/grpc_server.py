import grpc
from app.grpc.proto import recposts_pb2
from app.grpc.proto import recposts_pb2_grpc
import grpc.aio
from app.ml.recommender import recommender
from app.database.postgres import async_session_factory
from loguru import logger
import asyncio

from config import settings


class RecommendationServiceServicer(recposts_pb2_grpc.RecommendationServiceServicer):
    async def GetRecommendations(self, request, context):
        logger.info(f"gRPC request: user_id={request.user_id}, limit={request.limit}, exclude={request.exclude_author_posts}")

        try:
            async with async_session_factory() as session:
                posts_with_scores = await recommender.get_recommendations(
                    user_id=request.user_id,
                    limit=request.limit,
                    exclude_author_posts=request.exclude_author_posts,
                    session=session
                )

            response = recposts_pb2.GetRecommendationsResponse(
                user_id=request.user_id,
                total_count=len(posts_with_scores)
            )

            for post in posts_with_scores:
                response.recommendations.add(
                    post_id=post.id,
                    similarity_score=getattr(post, 'similarity_score', 0.0)
                )

            logger.info(f"Return {len(posts_with_scores)} recommendations")
            return response

        except Exception as e:
            logger.error(f"gRPC error: {e}")
            await context.abort(grpc.StatusCode.INTERNAL, str(e))

async def serve():
    server = grpc.aio.server()
    recposts_pb2_grpc.add_RecommendationServiceServicer_to_server(
        RecommendationServiceServicer(), server
    )
    port = settings.RECSYS_GRPC_PORT
    server.add_insecure_port(f'[::]:{port}')
    logger.info(f"gRPC server is running on port {port}")
    await server.start()
    await server.wait_for_termination()

if __name__ == '__main__':
    asyncio.run(serve())