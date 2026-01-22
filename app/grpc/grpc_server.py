import grpc
from concurrent import futures
from loguru import logger
import asyncio

from app.grpc.proto import recposts_pb2
from app.grpc.proto import recposts_pb2_grpc

from app.ml.recommender import recommender
from app.database.postgres import async_session_factory


class RecommendationServiceServicer(recposts_pb2_grpc.RecommendationServiceServicer):
    async def GetRecommendations(self, request, context):
        logger.info(f"gRPC request: user_id={request.user_id}, limit={request.limit}, exclude_author_posts={request.exclude_author_posts}")

        # event loop for async calls (gRPC sync, recommender async)
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)

        try:
            async with async_session_factory() as session:
                posts_with_scores = loop.run_until_complete(recommender.get_recommendations(
                    user_id=request.user_id,
                    limit=request.limit,
                    exclude_author_posts=request.exclude_author_posts,
                    session=session
                ))

            # mapping to protobuf-response
            response = recposts_pb2.GetRecommendationsResponse(
                user_id=request.user_id,
                total_count=len(posts_with_scores)
            )

            for post in posts_with_scores:
                response.recommendations.add(
                    post_id=post.id,
                    similarity_score=post.similarity_score or 0.0
                )

            return response

        except Exception as e:
            logger.error(f"gRPC error: {e}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(str(e))
            return recposts_pb2.GetRecommendationsResponse()

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    recposts_pb2_grpc.add_RecommendationServiceServicer_to_server(
        RecommendationServiceServicer(), server
    )
    port = 50051
    server.add_insecure_port(f'[::]:{port}')
    logger.info(f"gRPC server runs on port {port}")
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()