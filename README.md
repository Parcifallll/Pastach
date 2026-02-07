## Описание проекта
Pastach - RESTful социальная сеть с новостной лентой, в которой пользователи могут публиковать посты, ставить реакции и писать комментарии.

Рекомендации постов для пользователя вычисляются с помощью отдельного микросервиса на Python (генерирование с помощью LLM), общение с этим микросервисом происходит через Apache Kafka (отправка постов и реакций) и gRPC (получение id постов).

Цель данного пет-проекта - научиться работать с модулями фреймворка Spring, данными, инфраструктурой, тестированием, контейнеризацией.

## Стек технологий:
* Работа с данными: Spring Sata JPA, Hibernate, Flyway, Postgres, Redis
* Безопасность: Spring Security, JWT
* Тестирование: JUnit, Mockito, Testcontainers, WebMvcTest
* Прочее: websockets, gRPC; docker-compose

### Интерактивная документация: http://localhost:8080/docs/swagger-ui/index.html
### Miro-доска проекта: https://miro.com/app/board/uXjVJxKXx9g=/?share_link_id=797244928745

![img.png](img.png)

![img_1.png](img_1.png)

![img_2.png](img_2.png)

![img_3.png](img_3.png)

![img_4.png](img_4.png)

![img_5.png](img_5.png)

![img_6.png](img_6.png)