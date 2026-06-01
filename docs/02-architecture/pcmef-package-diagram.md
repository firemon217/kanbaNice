# Архитектура PCMEF — Диаграмма пакетов

## Диаграмма

```plantuml
@startuml
title Архитектура PCMEF — kanbaNice

allowmixing

' ========== INTERFACES ==========
interface "IUserService" as IUS {
  getProfile(id: Long): UserProfileResponseDTO
  updateProfile(id: Long, dto): UserProfileResponseDTO
  forgotPassword(email: String): String
  resetPassword(token, newPassword): String
  deleteAccount(id: Long): String
}

interface "ICompanyService" as ICS {
  getMyCompany(): CompanyResponseDTO
  createCompany(dto): CompanyResponseDTO
  updateMyCompany(dto): CompanyResponseDTO
  deleteMyCompany(): void
  addWorkerToMyCompany(dto): void
  deleteWorkerToMyCompany(id): void
}

interface "IProjectService" as IPS {
  listMyProjects(): List<ProjectResponseDTO>
  createProject(dto): ProjectResponseDTO
  getProject(id): ProjectResponseDTO
  deleteProject(id): void
  addWorkerToProject(projectId, userId): ProjectResponseDTO
}

interface "IBoardService" as IBS {
  listBoards(projectId): List<BoardResponseDTO>
  createBoard(projectId, dto): BoardResponseDTO
  updateBoard(boardId, dto): BoardResponseDTO
  deleteBoard(boardId): void
}

interface "ITaskService" as ITS {
  listTasks(boardId): List<TaskResponseDTO>
  createTask(boardId, dto): TaskResponseDTO
  getTask(taskId): TaskResponseDTO
  updateTask(taskId, dto): TaskResponseDTO
  deleteTask(taskId): void
}

interface "IUserRepository" as IUR
interface "IProjectRepository" as IPR
interface "IBoardRepository" as IBR
interface "ITaskRepository" as ITR
interface "IProjectMemberRepository" as IPMR

' ========== PRESENTATION ==========
package "Presentation (P)" {
    package "Web Client (React)" {
        rectangle "LoginPage" as LP
        rectangle "RegPage" as RP
        rectangle "CompanyPage" as CP
        rectangle "ProjectsSelectionPage" as PSP
        rectangle "ProjectPage" as PP
        rectangle "ProfilePage" as ProfP
        rectangle "OAuthRedirect" as OAR
    }
    package "Desktop Client (JavaFX)" {
        rectangle "LoginView" as LV
        rectangle "SignupView" as SV
        rectangle "CompanyView" as CV
        rectangle "ProjectsView" as PrV
        rectangle "ProjectBoardView" as PBV
        rectangle "ProfileView" as PrfV
        rectangle "MainLayout" as ML
    }
}

' ========== CONTROL ==========
package "Control (C) — controller/" {
    rectangle "AuthController" as AC
    rectangle "UserController" as UC
    rectangle "CompanyController" as CC
    rectangle "ProjectController" as PC
    rectangle "BoardController" as BC
    rectangle "TaskController" as TC
}

' ========== MEDIATOR ==========
package "Mediator (M) — service/" {
    rectangle "AuthService" as AS
    rectangle "UserService" as US
    rectangle "CompanyService" as CS
    rectangle "ProjectService" as PrS
    rectangle "BoardService" as BS
    rectangle "TaskService" as TS
    rectangle "EmailServiceImpl" as ES
}

' ========== ENTITY ==========
package "Entity (E) — entity/" {
    rectangle "User" as UE
    rectangle "Company" as CoE
    rectangle "KanbanProject" as KPE
    rectangle "KanbanBoard" as KBE
    rectangle "KanbanTask" as KTE
    rectangle "ProjectMember" as PME
}

' ========== FOUNDATION ==========
package "Foundation (F) — Repository/" {
    rectangle "UserRepository" as URep
    rectangle "KanbanProjectRepository" as KPRep
    rectangle "KanbanBoardRepository" as KBRep
    rectangle "KanbanTaskRepository" as KTRep
    rectangle "ProjectMemberRepository" as PMRep
}

database "PostgreSQL" as DB

' ========== DEPENDENCIES ==========
LP --> AC : HTTP REST
LV --> AC : HTTP REST

UC --> IUS : uses
CC --> ICS : uses
PC --> IPS : uses
BC --> IBS : uses
TC --> ITS : uses

US ..|> IUS : implements
CS ..|> ICS : implements
PrS ..|> IPS : implements
BS ..|> IBS : implements
TS ..|> ITS : implements

US --> IUR : uses
CS --> IUR : uses
PrS --> IPR : uses
PrS --> IPMR : uses
BS --> IBR : uses
TS --> ITR : uses

URep ..|> IUR : implements
KPRep ..|> IPR : implements
KBRep ..|> IBR : implements
KTRep ..|> ITR : implements
PMRep ..|> IPMR : implements

URep --> DB
KPRep --> DB
KBRep --> DB
KTRep --> DB
PMRep --> DB
@enduml
```

## Принципы PCMEF в проекте

| Принцип | Реализация |
|---------|-----------|
| Зависимости строго вниз | P → C → M → E → F; обратных зависимостей нет |
| Изоляция слоёв | Каждый контроллер делегирует сервису; сервис не знает о HTTP |
| Коммуникация через интерфейсы | Сервисы объявлены как интерфейсы; Spring Data JPA — интерфейсные репозитории |
| Отсутствие циклических зависимостей | Spring IoC обеспечивает ацикличный граф зависимостей |

## Соответствие пакетов слоям

| Слой PCMEF | Java-пакет | Количество классов |
|-----------|-----------|-------------------|
| P — Presentation | React `pages/`, `components/` / JavaFX `view/` | 7 страниц + 7 экранов |
| C — Control | `controller/` | 6 контроллеров |
| M — Mediator | `service/` | 6 сервисов + EmailService |
| E — Entity | `entity/` | 10 сущностей + перечислений |
| F — Foundation | `Repository/` | 5 репозиториев |
