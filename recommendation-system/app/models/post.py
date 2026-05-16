from datetime import datetime
from typing import Optional
from pydantic import BaseModel, Field


class PostBase(BaseModel):
    author_id: int = Field(..., alias="authorId")
    
    class Config:
        populate_by_name = True


class PostCreate(PostBase):
    id: int
    created_at: datetime = Field(default_factory=datetime.now, alias="createdAt")


class PostResponse(PostBase):
    id: int
    created_at: datetime = Field(..., alias="createdAt")
    
    class Config:
        from_attributes = True
        populate_by_name = True


class PostWithEmbedding(PostResponse):
    embedding: Optional[list[float]] = None
    similarity_score: Optional[float] = None
    recency_score: Optional[float] = None
