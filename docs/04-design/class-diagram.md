# Диаграмма классов проектирования

## Серверная часть (Backend)

```plantuml
@startuml
title Детальная диаграмма классов — Backend (kanbaNice)

' ===== ENTITY =====
package "entity" #FFEEBB {
    class User {
        -id: Long
        -name: String
        -username: String
        -email: String
        -password: String
        -userType: UserType
        -providerType: AuthProviderType
        -providerId: String
        -roles: Set<RoleType>
        -company: Company
        -resetToken: String
        -resetTokenExpiry: LocalDateTime
        +getAuthorities(): Collection<GrantedAuthority>
        +getUsername(): String
        +getPassword(): String
        +isEnabled(): boolean
    }

    class Company {
        -id: Long
        -name: String
        -users: Set<User>
    }

    class KanbanProject {
        -id: Long
        -name: String
        -company: Company
        -members: Set<ProjectMember>
        -boards: Set<KanbanBoard>
        -createdAt: LocalDateTime
        +prePersist(): void
    }

    class KanbanBoard {
        -id: Long
        -name: String
        -project: KanbanProject
        -tasks: Set<KanbanTask>
        -createdAt: LocalDateTime
        +prePersist(): void
    }

    class KanbanTask {
        -id: Long
        -title: String
        -description: String
        -status: TaskStatus
        -statusChangedBy: String
        -board: KanbanBoard
        -createdAt: LocalDateTime
        -updatedAt: LocalDateTime
        +prePersist(): void
        +preUpdate(): void
    }

    class ProjectMember {
        -id: Long
        -project: KanbanProject
        -user: User
        -role: ProjectMemberRole
    }

    enum TaskStatus { TODO; DONE }
    enum ProjectMemberRole { LEADER; WORKER }
    enum RoleType { ADMIN; USER; AUDITOR }
    enum AuthProviderType { LOCAL; GOOGLE }
}

' ===== REPOSITORY (Foundation) =====
package "Repository" #DDEEFF {
    interface UserRepository {
        +findByUsername(username: String): Optional<User>
        +findByEmail(email: String): Optional<User>
        +findByResetToken(token: String): Optional<User>
    }
    interface KanbanProjectRepository {
        +findByCompanyId(companyId: Long): List<KanbanProject>
    }
    interface KanbanBoardRepository {
        +findByProjectId(projectId: Long): List<KanbanBoard>
    }
    interface KanbanTaskRepository {
        +findByBoardId(boardId: Long): List<KanbanTask>
    }
    interface ProjectMemberRepository {
        +findByProjectIdAndUserId(pid: Long, uid: Long): Optional<ProjectMember>
    }
}

' ===== SERVICE (Mediator) =====
package "service" #EEFFDD {
    class AuthService {
        -userRepository: UserRepository
        -passwordEncoder: PasswordEncoder
        -jwtUtil: JwtUtil
        +login(dto: LoginRequestDTO): LoginResponseDTO
        +SignUp(dto: SignupRequestDTO): SignupResponseDTO
        +generateJWT(user: User): String
    }
    class UserService {
        -userRepository: UserRepository
        -emailService: EmailService
        +getProfile(id: Long): UserProfileResponseDTO
        +updateProfile(id: Long, dto: UpdateProfileDto): UserProfileResponseDTO
        +forgotPassword(email: String): String
        +resetPassword(token: String, newPassword: String): String
        +deleteAccount(id: Long): String
    }
    class CompanyService {
        -userRepository: UserRepository
        +getMyCompany(): CompanyResponseDTO
        +createCompany(dto: CompanyCreateDTO): CompanyResponseDTO
        +updateMyCompany(dto: CompanyUpdateDTO): CompanyResponseDTO
        +deleteMyCompany(): void
        +addWorkerToMyCompany(dto: WorkerRequestDTO): void
        +deleteWorkerToMyCompany(id: Long): void
    }
    class ProjectService {
        -projectRepository: KanbanProjectRepository
        -memberRepository: ProjectMemberRepository
        +listMyProjects(): List<ProjectResponseDTO>
        +createProject(dto: ProjectCreateDTO): ProjectResponseDTO
        +getProject(id: Long): ProjectResponseDTO
        +deleteProject(id: Long): void
        +addWorkerToProject(pid: Long, uid: Long): ProjectResponseDTO
    }
    class BoardService {
        -boardRepository: KanbanBoardRepository
        +listBoards(projectId: Long): List<BoardResponseDTO>
        +createBoard(projectId: Long, dto: BoardCreateDTO): BoardResponseDTO
        +updateBoard(boardId: Long, dto: BoardUpdateDTO): BoardResponseDTO
        +deleteBoard(boardId: Long): void
    }
    class TaskService {
        -taskRepository: KanbanTaskRepository
        -boardRepository: KanbanBoardRepository
        +listTasks(boardId: Long): List<TaskResponseDTO>
        +createTask(boardId: Long, dto: TaskCreateDTO): TaskResponseDTO
        +getTask(taskId: Long): TaskResponseDTO
        +updateTask(taskId: Long, dto: TaskUpdateDTO): TaskResponseDTO
        +deleteTask(taskId: Long): void
    }
}

' ===== CONTROLLER (Control) =====
package "controller" #FFDDEE {
    class AuthController {
        -authService: AuthService
        +login(dto: LoginRequestDTO): ResponseEntity<LoginResponseDTO>
        +signup(dto: SignupRequestDTO): ResponseEntity<SignupResponseDTO>
    }
    class UserController {
        -userService: UserService
        +getProfile(): ResponseEntity<UserProfileResponseDTO>
        +updateProfile(dto: UpdateProfileDto): ResponseEntity<UserProfileResponseDTO>
        +forgotPassword(email: String): ResponseEntity<String>
        +resetPassword(token, newPwd): ResponseEntity<String>
        +deleteAccount(): ResponseEntity<String>
    }
    class CompanyController {
        -companyService: CompanyService
        +getMyCompany(): ResponseEntity<CompanyResponseDTO>
        +createCompany(dto): ResponseEntity<CompanyResponseDTO>
        +updateMyCompany(dto): ResponseEntity<CompanyResponseDTO>
        +deleteMyCompany(): ResponseEntity<Void>
        +addWorkerToMyCompany(dto): ResponseEntity<Void>
        +deleteWorkerToMyCompany(id): ResponseEntity<Void>
    }
    class ProjectController {
        -projectService: ProjectService
    }
    class BoardController {
        -boardService: BoardService
    }
    class TaskController {
        -taskService: TaskService
    }
}

' Relations
AuthController --> AuthService
UserController --> UserService
CompanyController --> CompanyService
ProjectController --> ProjectService
BoardController --> BoardService
TaskController --> TaskService

AuthService --> UserRepository
UserService --> UserRepository
CompanyService --> UserRepository
ProjectService --> KanbanProjectRepository
ProjectService --> ProjectMemberRepository
BoardService --> KanbanBoardRepository
TaskService --> KanbanTaskRepository
TaskService --> KanbanBoardRepository

Company "1" *-- "0..*" User
Company "1" *-- "0..*" KanbanProject
KanbanProject "1" *-- "0..*" KanbanBoard
KanbanProject "1" *-- "0..*" ProjectMember
KanbanBoard "1" *-- "0..*" KanbanTask
@enduml
```
