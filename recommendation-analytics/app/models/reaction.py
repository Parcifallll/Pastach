from enum import Enum
from pydantic import BaseModel

class ReactionType(str, Enum):
    LIKE = "LIKE"
    DISLIKE = "DISLIKE"

class ReactionWithWeight(BaseModel):
    reaction: ReactionType
    weight: float = 0.0

REACTION_WEIGHTS = {
    ReactionType.LIKE: 1.0,
    ReactionType.DISLIKE: -0.5,
}