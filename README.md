# Обзор Реализации Безопасности

Это приложение реализует модель безопасности с использованием Spring Security с интеграцией JAAS и JWT для аутентификации. Учетные записи пользователей хранятся в XML-файле.

## Модель Безопасности

### Привилегии

Система реализует следующие привилегии:

1. `VIEW_VIDEO`: Позволяет пользователям просматривать видео
2. `CREATE_COMPLAINT`: Позволяет пользователям отправлять жалобы
3. `CREATE_VIDEO`: Позволяет пользователям загружать новые видео
4. `REVIEW_VIDEO`: Позволяет пользователям проверять и модерировать видео

### Роли

Система реализует следующие роли, каждая со своим набором привилегий:

1. `UNAUTHENTICATED`: Без привилегий
2. `USER`: Имеет привилегии `VIEW_VIDEO`, `CREATE_COMPLAINT`, `CREATE_VIDEO`
3. `ADMIN`: Имеет все привилегии: `VIEW_VIDEO`, `CREATE_COMPLAINT`, `CREATE_VIDEO`, `REVIEW_VIDEO`, а также доступ к admin-only эндпоинтам
4. `UNAPPROVED_ADMIN`: Имеет те же привилегии что и `USER`, но требует активации от другого администратора

## Детали Реализации

### Хранение Пользователей

Учетные записи пользователей хранятся в XML-файле (`users.xml`) в корне приложения. Этот файл создается автоматически при запуске приложения, если он не существует.
