package GameShelfRequirements {
    doc /* Требования к настольному приложению GameShelf
           (менеджер игрового бэклога и статистики).
           Версия 0.2 (после рецензирования). */

    private import ScalarValues::*;

    // ---------- Участники и система ----------
    part def Player;
    part def SteamWebAPI;
    part def RawgAPI;
    part def OperatingSystem;
    part def OsNotifications;

    enum def GameStatus {
        enum BACKLOG;
        enum PLAYING;
        enum ON_HOLD;
        enum COMPLETED;
        enum COMPLETED_100;
        enum DROPPED;
    }

    part def GameShelf {
        part libraryModule;
        part progressModule;
        part timeTrackingModule;
        part statisticsModule;
        part integrationModule;
        part storageModule;
    }

    // ---------- Модель предметной области ----------
    part def Platform  { attribute name : String; attribute store : String; }
    part def Collection { attribute name : String; }
    part def Session {
        attribute start : String;          // ISO-8601 дата и время
        attribute end : String[0..1];      // пусто, пока сессия активна
        attribute source : String;         // TIMER | AUTO | MANUAL | STEAM_IMPORT
        attribute comment : String;
    }
    part def StatusChange {
        attribute fromStatus : GameStatus;
        attribute toStatus : GameStatus;
        attribute at : String;
        attribute playthroughNo : Integer;
    }
    part def Game {
        attribute title : String;
        attribute status : GameStatus;
        attribute estimateHours : Real[0..1];
        attribute playedBeforeTrackingMin : Integer;
        attribute rating : Integer[0..1];
        attribute notes : String;
        attribute executable : String[0..1];
        attribute steamAppId : Integer[0..1];
        attribute playthroughNo : Integer;
        ref platform : Platform[1];
        ref collections : Collection[0..*];
        part sessions : Session[0..*];
        part history : StatusChange[0..*];
    }
    part def YearGoal { attribute year : Integer; attribute targetCompleted : Integer; }

    // ---------- Базовые определения требований ----------
    requirement def FunctionalRequirement {
        attribute priority : String;   // MoSCoW: Must | Should | Could
        attribute iteration : Integer;
        subject system : GameShelf;
    }
    requirement def QualityRequirement {
        attribute metric : String;
        subject system : GameShelf;
    }

    // ---------- Функциональные требования ----------
    requirement gameShelfSpec {
        doc /* Корневое требование: учёт игровой библиотеки, статусов
               прохождения и игрового времени с получением статистики. */

        requirement libraryGroup {
            doc /* Библиотека игр. */
            requirement <'REQ-FUNC-001'> func001 : FunctionalRequirement {
                doc /* Добавление игры вручную: название и платформа обязательны. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-002'> func002 : FunctionalRequirement {
                doc /* Поиск метаданных (жанр, год, обложка, среднее время) в RAWG. */
                attribute :>> priority = "Should"; attribute :>> iteration = 2;
            }
            requirement <'REQ-FUNC-003'> func003 : FunctionalRequirement {
                doc /* Импорт библиотеки и наигранного времени из Steam. */
                attribute :>> priority = "Should"; attribute :>> iteration = 2;
            }
            requirement <'REQ-FUNC-004'> func004 : FunctionalRequirement {
                doc /* Редактирование и удаление игры с подтверждением. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-010'> func010 : FunctionalRequirement {
                doc /* Просмотр бэклога: фильтры, сортировка, поиск. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-011'> func011 : FunctionalRequirement {
                doc /* Карточка игры: история сессий и статусов, оценка, заметки. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-018'> func018 : FunctionalRequirement {
                doc /* Справочник платформ и пользовательские коллекции. */
                attribute :>> priority = "Should"; attribute :>> iteration = 2;
            }
        }

        requirement progressGroup {
            doc /* Статусы прохождения. */
            requirement <'REQ-FUNC-005'> func005 : FunctionalRequirement {
                doc /* Шесть статусов и матрица допустимых переходов. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-019'> func019 : FunctionalRequirement {
                doc /* Повторное прохождение (добавлено после рецензирования). */
                attribute :>> priority = "Should"; attribute :>> iteration = 2;
            }
            requirement <'REQ-FUNC-020'> func020 : FunctionalRequirement {
                doc /* Напоминание о забытых играх (добавлено после рецензирования). */
                attribute :>> priority = "Could"; attribute :>> iteration = 3;
            }
        }

        requirement timeGroup {
            doc /* Учёт игрового времени. */
            requirement <'REQ-FUNC-006'> func006 : FunctionalRequirement {
                doc /* Таймер сессии; одна активная сессия; сессии < 60 с не сохраняются. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-007'> func007 : FunctionalRequirement {
                doc /* Автоотслеживание процесса игры; объединение при перерыве <= 5 мин. */
                attribute :>> priority = "Should"; attribute :>> iteration = 2;
            }
            requirement <'REQ-FUNC-008'> func008 : FunctionalRequirement {
                doc /* Ручное добавление, изменение и удаление сессий. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-009'> func009 : FunctionalRequirement {
                doc /* Расчёт наигранного и оставшегося времени, формула (2.1). */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
        }

        requirement statsGroup {
            doc /* Статистика и цели. */
            requirement <'REQ-FUNC-012'> func012 : FunctionalRequirement {
                doc /* Панель статистики за период, включая активность по дням недели. */
                attribute :>> priority = "Must"; attribute :>> iteration = 1;
            }
            requirement <'REQ-FUNC-013'> func013 : FunctionalRequirement {
                doc /* Прогноз разбора бэклога, формула (3.1). */
                attribute :>> priority = "Should"; attribute :>> iteration = 2;
            }
            requirement <'REQ-FUNC-014'> func014 : FunctionalRequirement {
                doc /* Цели на год и прогресс их выполнения. */
                attribute :>> priority = "Could"; attribute :>> iteration = 3;
            }
            requirement <'REQ-FUNC-015'> func015 : FunctionalRequirement {
                doc /* Подбор игры из бэклога по доступному времени. */
                attribute :>> priority = "Could"; attribute :>> iteration = 3;
            }
        }

        requirement dataGroup {
            doc /* Данные и резервирование. */
            requirement <'REQ-FUNC-016'> func016 : FunctionalRequirement {
                doc /* Резервная копия JSON и восстановление. */
                attribute :>> priority = "Should"; attribute :>> iteration = 2;
            }
            requirement <'REQ-FUNC-017'> func017 : FunctionalRequirement {
                doc /* Экспорт статистики и сессий в CSV. */
                attribute :>> priority = "Could"; attribute :>> iteration = 3;
            }
        }
    }

    // ---------- Нефункциональные требования ----------
    requirement qualitySpec {
        requirement interfaces {
            requirement <'REQ-INT-001'> int001 : QualityRequirement { attribute :>> metric = "Steam Web API: IPlayerService/GetOwnedGames v1, таймаут 10 с"; }
            requirement <'REQ-INT-002'> int002 : QualityRequirement { attribute :>> metric = "RAWG API v1, не более 1 запроса в секунду"; }
            requirement <'REQ-INT-003'> int003 : QualityRequirement { attribute :>> metric = "JSON UTF-8 со схемой версии formatVersion"; }
            requirement <'REQ-INT-004'> int004 : QualityRequirement { attribute :>> metric = "CSV RFC 4180, UTF-8 с BOM, разделитель ;"; }
        }
        requirement serviceQuality {
            requirement <'REQ-PERF-001'> perf001 : QualityRequirement { attribute :>> metric = "Запуск до главного окна <= 3 с"; }
            requirement <'REQ-PERF-002'> perf002 : QualityRequirement { attribute :>> metric = "Фильтрация 5000 игр <= 300 мс"; }
            requirement <'REQ-PERF-003'> perf003 : QualityRequirement { attribute :>> metric = "Статистика по 50 000 сессий <= 1 с"; }
            requirement <'REQ-PERF-004'> perf004 : QualityRequirement { attribute :>> metric = "Автоотслеживание: CPU <= 1 %, опрос раз в 5 с"; }
            requirement <'REQ-SEC-001'> sec001 : QualityRequirement { attribute :>> metric = "Ключ Steam API зашифрован AES-256-GCM"; }
            requirement <'REQ-SEC-002'> sec002 : QualityRequirement { attribute :>> metric = "Внешние запросы только по HTTPS (TLS 1.2+)"; }
            requirement <'REQ-SEC-003'> sec003 : QualityRequirement { attribute :>> metric = "Нет передачи данных третьим лицам"; }
            requirement <'REQ-REL-001'> rel001 : QualityRequirement { attribute :>> metric = "Потеря времени активной сессии при сбое <= 60 с"; }
            requirement <'REQ-REL-002'> rel002 : QualityRequirement { attribute :>> metric = "Изменения в транзакциях SQLite, журнал WAL"; }
            requirement <'REQ-REL-003'> rel003 : QualityRequirement { attribute :>> metric = "Отказ внешнего API не блокирует интерфейс"; }
            requirement <'REQ-AVAIL-001'> avail001 : QualityRequirement { attribute :>> metric = "Все функции, кроме импорта и поиска, работают офлайн"; }
            requirement <'REQ-OBS-001'> obs001 : QualityRequirement { attribute :>> metric = "Журнал: 5 файлов по 10 МБ, без ключей API"; }
        }
        requirement compliance {
            requirement <'REQ-COMP-001'> comp001 : QualityRequirement { attribute :>> metric = "Закон РБ № 99-З: данные хранятся только локально"; }
            requirement <'REQ-COMP-002'> comp002 : QualityRequirement { attribute :>> metric = "Соблюдение условий Steam Web API и RAWG, ссылка на RAWG"; }
        }
        requirement implementation {
            requirement <'REQ-INST-001'> inst001 : QualityRequirement { attribute :>> metric = "Установщик jpackage: MSI и DEB, JRE в комплекте"; }
            requirement <'REQ-BUILD-001'> build001 : QualityRequirement { attribute :>> metric = "Maven, сборка на GitHub Actions по каждому PR"; }
            requirement <'REQ-MAINT-001'> maint001 : QualityRequirement { attribute :>> metric = "Покрытие модулей статусов и времени >= 80 %"; }
            requirement <'REQ-PORT-001'> port001 : QualityRequirement { attribute :>> metric = "Windows 10/11 x64, Ubuntu 22.04+ x64"; }
            requirement <'REQ-COST-001'> cost001 : QualityRequirement { attribute :>> metric = "Эксплуатационные расходы 0 USD"; }
            requirement <'REQ-DEAD-001'> dead001 : QualityRequirement { attribute :>> metric = "Три итерации до конца семестра"; }
            requirement <'REQ-CM-001'> cm001 : QualityRequirement { attribute :>> metric = "Изменения через pull request и историю версий SRS"; }
        }
    }

    // ---------- Связи «удовлетворяет» ----------
    satisfy gameShelfSpec.libraryGroup by GameShelf::libraryModule;
    satisfy gameShelfSpec.progressGroup by GameShelf::progressModule;
    satisfy gameShelfSpec.timeGroup by GameShelf::timeTrackingModule;
    satisfy gameShelfSpec.statsGroup by GameShelf::statisticsModule;
    satisfy gameShelfSpec.dataGroup by GameShelf::storageModule;
    satisfy qualitySpec.interfaces by GameShelf::integrationModule;

    // ---------- Варианты использования ----------
    use case def AddGame          { subject system : GameShelf; actor player : Player; }
    use case def ChangeStatus     { subject system : GameShelf; actor player : Player; }
    use case def TrackSession     { subject system : GameShelf; actor player : Player; actor os : OperatingSystem; }
    use case def ViewBacklog      { subject system : GameShelf; actor player : Player; }
    use case def ViewStatistics   { subject system : GameShelf; actor player : Player; }
    use case def SetYearGoals     { subject system : GameShelf; actor player : Player; }
    use case def BackupData       { subject system : GameShelf; actor player : Player; }
    use case def ImportSteam      { subject system : GameShelf; actor player : Player; actor steam : SteamWebAPI; }
    use case def FindMetadata     { subject system : GameShelf; actor rawg : RawgAPI; }
    use case def RemindForgotten  { subject system : GameShelf; actor notifications : OsNotifications; }
}
