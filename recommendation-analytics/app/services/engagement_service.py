import logging
from dataclasses import dataclass
from typing import Optional

logger = logging.getLogger(__name__)

REACTION_LIKE_WEIGHT = 0.25
REACTION_DISLIKE_WEIGHT = -0.10

WEIGHTS_RECOMMENDED = {
    "v": 0.05,
    "d": 0.35,
    "r": 0.25,
    "s": 0.15,
    "sim": 0.10,
    "rec": 0.10,
}

WEIGHTS_NON_RECOMMENDED = {
    "v": 0.063,
    "d": 0.438,
    "r": 0.313,
    "s": 0.188,
}


@dataclass
class PostRow:
    is_recommended: bool
    viewed_at: Optional[object]
    view_duration: Optional[float]
    reaction: Optional[str]
    weighted_sentiment_score: float
    similarity_score: Optional[float]
    recency_score: Optional[float]


class EngagementService:

    def compute(self, row: PostRow) -> float:
        v = self._compute_v(row.viewed_at)
        d = self._compute_d(row.view_duration)
        r = self._compute_r(row.reaction)
        s = self._compute_s(row.weighted_sentiment_score)

        # E = 0.05·V + 0.35·D(t) + 0.25·R + 0.15·S + 0.10·sim + 0.10·rec_norm
        if row.is_recommended:
            sim = float(row.similarity_score) if row.similarity_score is not None else 0.0
            rec_norm = float(row.recency_score) - 1.0 if row.recency_score is not None else 0.0
            w = WEIGHTS_RECOMMENDED
            score = (
                    w["v"] * v +
                    w["d"] * d +
                    w["r"] * r +
                    w["s"] * s +
                    w["sim"] * sim +
                    w["rec"] * rec_norm
            )

        # E = 0.063·V + 0.438·D(t) + 0.313·R + 0.188·S
        else:
            w = WEIGHTS_NON_RECOMMENDED
            score = (
                    w["v"] * v +
                    w["d"] * d +
                    w["r"] * r +
                    w["s"] * s
            )

        result = round(float(score), 6)
        logger.debug(
            f"Engagement: v={v}, d={d:.4f}, r={r}, s={s:.4f}, "
            f"recommended={row.is_recommended} → E={result}"
        )
        return result

    def _compute_v(self, viewed_at) -> float:
        return 1.0 if viewed_at is not None else 0.0

    def _compute_d(self, view_duration: Optional[float]) -> float:
        if view_duration is None:
            return 0.0
        t = view_duration
        if t < 3:
            return 0.0
        if t > 300:
            return 1.0
        return (t - 3) / 297

    def _compute_r(self, reaction: Optional[str]) -> float:
        if reaction == "LIKE":
            return REACTION_LIKE_WEIGHT
        if reaction == "DISLIKE":
            return REACTION_DISLIKE_WEIGHT
        return 0.0

    def _compute_s(self, weighted_sentiment_score: float) -> float:
        return (weighted_sentiment_score + 1) / 2
