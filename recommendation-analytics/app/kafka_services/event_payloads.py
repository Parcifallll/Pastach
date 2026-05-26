from pydantic import BaseModel

class RecommendationCreatedPayload(BaseModel):
    userId: int
    postId: int
    authorId: int
    createdAt: str
    similarityScore: float
    recencyScore: float


class RecommendationViewedPayload(BaseModel):
    userId: int
    postId: int
    viewedAt: str
    viewDuration: float


class RecommendationReactedPayload(BaseModel):
    userId: int
    postId: int
    reaction: str


class RecommendationSentimentPayload(BaseModel):
    userId: int
    postId: int
    commentText: str


class RecommendationEvent(BaseModel):
    eventType: str
    eventId: str
    timestamp: str
    payload: dict