from pydantic import BaseModel, Field
from app.models.post import PostResponse


class UserPreferences(BaseModel):
    user_id: int = Field(..., alias="userId")
    liked_post_ids: list[int] = Field(default_factory=list, alias="likedPostIds")
    disliked_post_ids: list[int] = Field(default_factory=list, alias="dislikedPostIds")
    preference_embedding: list[float] | None = Field(None, alias="preferenceEmbedding")
    
    class Config:
        populate_by_name = True


class RecommendationRequest(BaseModel):
    user_id: int = Field(..., alias="userId")
    limit: int = Field(10, ge=1, le=50)
    exclude_author_posts: bool = Field(True, alias="excludeAuthorPosts")
    
    class Config:
        populate_by_name = True


class RecommendationResponse(BaseModel):
    user_id: int = Field(..., alias="userId")
    recommendations: list[PostResponse]
    total_count: int = Field(..., alias="totalCount")
    
    class Config:
        populate_by_name = True
