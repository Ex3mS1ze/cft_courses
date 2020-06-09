Можно импортировать в Postman [несколько запросов.](https://www.getpostman.com/collections/bed46ba41dc3ff4d9ec4) 

БД инициализируется несколькими записями (см `data.sql`), чтобы изменить поведение в `application.properties`
`spring.datasource.initialization-mode` заменить на `none`.

Данные для "постоянных" пользователей (email:password):
- user@m.ru : ad
- critic@m.ru : ad
- admin@m.ru : ad