from datetime import datetime
from enum import Enum
from pydantic import BaseModel, Field


class ReactionType(str, Enum):
    LIKE = "LIKE"
    DISLIKE = "DISLIKE"


class ReactionTargetType(str, Enum):
    POST = "POST"
    COMMENT = "COMMENT"


class ReactionBase(BaseModel):
    target_type: ReactionTargetType = Field(..., alias="targetType")
    target_id: int = Field(..., alias="targetId")
    author_id: int = Field(..., alias="authorId")
    type: ReactionType

    class Config:
        populate_by_name = True


class ReactionCreate(ReactionBase):
    id: int
    created_at: datetime = Field(default_factory=datetime.now, alias="createdAt")


class ReactionResponse(ReactionBase):
    id: int
    created_at: datetime = Field(..., alias="createdAt")

    class Config:
        from_attributes = True
        populate_by_name = True
