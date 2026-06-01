# Диаграмма зависимостей

## Зависимости между компонентами системы

```plantuml
@startuml
title Диаграмма зависимостей — kanbaNice

left to right direction

package "Presentation" #FFEEBB {
    component [React Web Client] as WEB
    component [JavaFX Desktop] as DESK
}

package "Backend" #DDEEFF {
    package "Control" {
        component [AuthController] as AC
        component [UserController] as UC
        component [CompanyController] as CC
        component [ProjectController] as PC
        component [BoardController] as BC
        component [TaskController] as TC
    }

    package "Security" {
        component [JwtAuthFilter] as JWT
        component [SecurityConfig] as SEC
        component [OAuth2Handler] as OA
    }

    package "Mediator" {
        component [AuthService] as AS
        component [UserService] as US
        component [CompanyService] as CS
        component [ProjectService] as PRS
        component [BoardService] as BS
        component [TaskService] as TS
        component [EmailService] as ES
    }

    package "Entity" {
        component [User] as UE
        component [Company] as CE
        component [KanbanProject] as KPE
        component [KanbanBoard] as KBE
        component [KanbanTask] as KTE
        component [ProjectMember] as PME
    }

    package "Foundation" {
        component [UserRepository] as UR
        component [KanbanProjectRepo] as KPR
        component [KanbanBoardRepo] as KBR
        component [KanbanTaskRepo] as KTR
        component [ProjectMemberRepo] as PMR
    }
}

database "PostgreSQL" as DB

' Client → Backend
WEB --> AC : HTTP/REST (JWT)
WEB --> UC : HTTP/REST (JWT)
WEB --> CC : HTTP/REST (JWT)
WEB --> PC : HTTP/REST (JWT)
WEB --> BC : HTTP/REST (JWT)
WEB --> TC : HTTP/REST (JWT)
DESK --> AC : HTTP/REST (JWT)
DESK --> TC : HTTP/REST (JWT)

' Security Filter
JWT --> SEC : uses
SEC --> OA : configures

' Control → Mediator
AC --> AS
UC --> US
CC --> CS
PC --> PRS
BC --> BS
TC --> TS

' Mediator → Foundation
AS --> UR
US --> UR
US --> ES
CS --> UR
PRS --> KPR
PRS --> PMR
BS --> KBR
TS --> KTR
TS --> KBR

' Foundation → DB
UR --> DB
KPR --> DB
KBR --> DB
KTR --> DB
PMR --> DB

' Entity relationships
UE --> CE : ManyToOne
PME --> KPE : ManyToOne
PME --> UE : ManyToOne
KPE --> CE : ManyToOne
KBE --> KPE : ManyToOne
KTE --> KBE : ManyToOne
@enduml
```

## Правило «нет зависимости вверх»

| От слоя | К слою | Разрешено |
|---------|--------|----------|
| Presentation | Control | ✅ (HTTP) |
| Control | Mediator | ✅ |
| Mediator | Entity | ✅ |
| Mediator | Foundation | ✅ |
| Foundation | Entity | ✅ |
| Entity | Foundation | ❌ Запрещено |
| Foundation | Control | ❌ Запрещено |
| Mediator | Control | ❌ Запрещено |

Граф зависимостей **ацикличен** — нарушений принципа не обнаружено.
