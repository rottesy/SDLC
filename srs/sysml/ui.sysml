package GameShelfUI {
    doc /* Прототип графического интерфейса GameShelf в нотации SysML v2.
           Описывает экраны, их элементы и переходы между ними. Изображения
           экранов (мокапы) — srs/img/mock-*.png, исходники — srs/mockups/*.html.
           Версия 0.2. */

    private import ScalarValues::*;
    private import GameShelfRequirements::*;

    // ---------- Базовые элементы интерфейса ----------
    part def Widget        { attribute label : String; }
    part def Button :> Widget;
    part def TextField :> Widget { attribute placeholder : String; }
    part def ChipGroup :> Widget { attribute options : String[1..*]; attribute multiSelect : Boolean; }
    part def DataTable :> Widget { attribute columns : String[1..*]; }
    part def Chart :> Widget     { attribute kind : String; }   // bar | heatmap | kpi
    part def Screen              { attribute title : String; }

    part def MainWindow {
        attribute minWidth : Integer = 1100;
        attribute minHeight : Integer = 640;
        part sidebar : Widget {
            doc /* Навигация: Библиотека, Статистика, Сессии, Цели на год, Импорт, Настройки;
                   внизу — индикатор активной сессии и состояние подключения Steam. */
        }
        part content : Screen;
    }

    // ---------- Экраны ----------
    part libraryScreen : Screen {                       // рисунок 3.4, mock-library.png
        attribute :>> title = "Библиотека";
        part search : TextField   { attribute :>> placeholder = "Поиск по названию…"; }
        part whatToPlay : Button  { attribute :>> label = "Что поиграть?"; }
        part addGame : Button     { attribute :>> label = "+ Добавить игру"; }
        part statusFilter : ChipGroup {
            attribute :>> options = ("Все", "Бэклог", "Играю", "Отложено", "Пройдено", "100 %", "Брошено");
            attribute :>> multiSelect = true;
        }
        part platformFilter : ChipGroup { attribute :>> multiSelect = true; }
        part games : DataTable {
            attribute :>> columns = ("Название", "Платформа", "Статус", "Сыграно", "Оценка, ч",
                                     "Прогресс", "Осталось", "Последний запуск", "Запуск");
        }
        part summary : Widget { doc /* Итог: осталось часов в бэклоге и прогноз разбора (REQ-FUNC-013). */ }
    }

    part gameScreen : Screen {                          // рисунок 3.5, mock-game.png
        attribute :>> title = "Карточка игры";
        part statusSelector : ChipGroup { attribute :>> multiSelect = false; }
        part sessionButton : Button { doc /* «Начать сессию» / «Остановить сессию ЧЧ:ММ:СС». */ }
        part replayButton : Button  { attribute :>> label = "Пройти заново"; }   // REQ-FUNC-019, v0.2
        part counters : Widget { doc /* Сыграно, оценка, осталось, номер прохождения. */ }
        part sessions : DataTable {
            attribute :>> columns = ("Дата", "Начало", "Окончание", "Длительность", "Источник", "Комментарий");
        }
        part addSession : Button { attribute :>> label = "+ Добавить сессию вручную"; }
    }

    part statsScreen : Screen {                         // рисунок 3.6, mock-stats.png
        attribute :>> title = "Статистика";
        part period : ChipGroup { attribute :>> options = ("Месяц", "Год", "Всё время"); }
        part kpis : Chart { attribute :>> kind = "kpi"; }
        part byMonth : Chart { attribute :>> kind = "bar"; }
        part topGames : DataTable;
        part weekdayHeatmap : Chart { attribute :>> kind = "heatmap"; }   // дополнение REQ-FUNC-012, v0.2
        part exportCsv : Button { attribute :>> label = "Экспорт CSV"; }
    }

    part addGameScreen : Screen {                       // рисунок 3.7, mock-add-game.png
        attribute :>> title = "Добавление игры";
        part rawgSearch : TextField;
        part rawgResults : DataTable;
        part form : Widget { doc /* Название*, платформа*, статус*, оценка, жанр, дата покупки,
                                     исполняемый файл, коллекции, время до учёта. */ }
        part save : Button { attribute :>> label = "Сохранить"; }
    }

    part steamImportScreen : Screen {                   // рисунок 3.8, mock-steam-import.png
        attribute :>> title = "Импорт библиотеки Steam";
        part steamId : TextField;
        part apiKey : TextField;
        part preview : DataTable {
            attribute :>> columns = ("Выбор", "Игра", "AppID", "Время в Steam", "Последний запуск",
                                     "Будет установлен статус", "Действие");
        }
        part runImport : Button { attribute :>> label = "Импортировать выбранные"; }
    }

    // ---------- Навигация ----------
    state def Navigation {
        entry; then library;
        state library;
        state game;
        state stats;
        state addGame;
        state steamImport;
        transition library_to_game  first library  accept OpenGame     then game;
        transition library_to_add   first library  accept AddClicked   then addGame;
        transition add_to_library   first addGame  accept Saved        then library;
        transition game_to_library  first game     accept Back         then library;
        transition any_to_stats     first library  accept StatsClicked then stats;
        transition any_to_import    first library  accept ImportClicked then steamImport;
        transition import_to_lib    first steamImport accept Imported  then library;
    }
    attribute def OpenGame; attribute def AddClicked; attribute def Saved; attribute def Back;
    attribute def StatsClicked; attribute def ImportClicked; attribute def Imported;
}
