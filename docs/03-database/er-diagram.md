# ER-диаграмма (логическая модель данных)

## Диаграмма

```plantuml
@startuml
title ER-диаграмма — kanbaNice (PostgreSQL)

entity "app_user" as USER {
  *id : BIGSERIAL <<PK>>
  --
  *name : VARCHAR(255)
  *username : VARCHAR(255) <<UNIQUE>>
  *email : VARCHAR(255) <<UNIQUE>>
  *password : VARCHAR(255)
  *provider_type : VARCHAR(20)
  user_type : VARCHAR(20)
  provider_id : VARCHAR(255)
  reset_token : VARCHAR(255)
  reset_token_expiry : TIMESTAMP
  last_password_reset_request : TIMESTAMP
  pending_email : VARCHAR(255)
  email_verification_token : VARCHAR(255)
  email_verification_expiry : TIMESTAMP
  company_id : BIGINT <<FK>>
}

entity "user_roles" as ROLES {
  *user_id : BIGINT <<FK>>
  *roles : VARCHAR(20)
}

entity "company" as COMPANY {
  *id : BIGSERIAL <<PK>>
  --
  *name : VARCHAR(255)
}

entity "kanban_project" as PROJECT {
  *id : BIGSERIAL <<PK>>
  --
  *name : VARCHAR(255)
  *company_id : BIGINT <<FK>>
  created_at : TIMESTAMP
}

entity "project_member" as MEMBER {
  *id : BIGSERIAL <<PK>>
  --
  *project_id : BIGINT <<FK>>
  *user_id : BIGINT <<FK>>
  *role : VARCHAR(20)
  <<UNIQUE(project_id, user_id)>>
}

entity "kanban_board" as BOARD {
  *id : BIGSERIAL <<PK>>
  --
  *name : VARCHAR(255)
  *project_id : BIGINT <<FK>>
  created_at : TIMESTAMP
}

entity "kanban_task" as TASK {
  *id : BIGSERIAL <<PK>>
  --
  *title : VARCHAR(255)
  description : TEXT
  *status : VARCHAR(20)
  status_changed_by : VARCHAR(255)
  *board_id : BIGINT <<FK>>
  created_at : TIMESTAMP
  updated_at : TIMESTAMP
}

COMPANY ||--o{ USER : "employs"
COMPANY ||--o{ PROJECT : "owns"
PROJECT ||--o{ MEMBER : "has"
PROJECT ||--o{ BOARD : "contains"
BOARD ||--o{ TASK : "holds"
USER ||--o{ MEMBER : "participates via"
USER ||--o{ ROLES : "has"
@enduml
```

## Описание таблиц

| Таблица | PK | Описание |
|---------|-----|---------|
| `app_user` | id | Учётные записи пользователей |
| `user_roles` | (user_id, roles) | Роли пользователя (ADMIN/USER/AUDITOR) |
| `company` | id | Организации |
| `kanban_project` | id | Проекты внутри компании |
| `project_member` | id | Участники проекта с ролью (LEADER/WORKER) |
| `kanban_board` | id | Канбан-доски проекта |
| `kanban_task` | id | Задачи на доске |

## Индексы

| Таблица | Поле(я) | Тип | Назначение |
|---------|---------|-----|-----------|
| `app_user` | `username` | UNIQUE | Быстрый поиск при аутентификации |
| `app_user` | `email` | UNIQUE | Поиск при OAuth2 и сбросе пароля |
| `project_member` | `(project_id, user_id)` | UNIQUE | Предотвращение дублирования участников |
| `kanban_project` | `company_id` | INDEX | Фильтрация проектов по компании |
| `kanban_board` | `project_id` | INDEX | Фильтрация досок по проекту |
| `kanban_task` | `board_id` | INDEX | Фильтрация задач по доске |

## Нормализация

- **1NF:** Все столбцы атомарны; роли вынесены в отдельную таблицу `user_roles`
- **2NF:** Все атрибуты зависят от первичного ключа (нет частичных зависимостей)
- **3NF:** Нет транзитивных зависимостей; все FK ссылаются на PK родительских таблиц
