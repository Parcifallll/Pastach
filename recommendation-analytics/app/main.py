import logging

from app.config import settings
from app.kafka_services.kafka_listener import RecommendationAnalyticsListener

logging.basicConfig(
    level=settings.LOG_LEVEL,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)

listener = RecommendationAnalyticsListener()
listener.start()
 