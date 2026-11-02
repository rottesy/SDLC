#!/usr/bin/env python3
"""Генерация диаграмм SysML v2 (графическая нотация) для GameShelf в SVG.

Запуск:  python3 diagrams.py  -> создаёт *.svg в srs/sysml/out,
затем PNG получаются рендером в headless Chrome (см. render.sh).
"""
import os
from html import escape

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "out")
os.makedirs(OUT, exist_ok=True)
FONT = "DejaVu Sans, Liberation Sans, Arial, sans-serif"
NEW = "#ffd9a0"     # требования, добавленные после рецензирования
NEW_STROKE = "#d98a2b"


class Svg:
    def __init__(self, w, h):
        self.w, self.h, self.el = w, h, []

    def add(self, s):
        self.el.append(s)

    def rect(self, x, y, w, h, fill="#fff", stroke="#000", sw=1.2, rx=0):
        self.add(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{rx}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}"/>')

    def text(self, x, y, s, size=13, weight="normal", anchor="middle", fill="#000"):
        self.add(f'<text x="{x}" y="{y}" font-size="{size}" font-weight="{weight}" text-anchor="{anchor}" fill="{fill}" font-family="{FONT}">{escape(s)}</text>')

    def line(self, x1, y1, x2, y2, dash=False, sw=1.2):
        d = ' stroke-dasharray="6,4"' if dash else ""
        self.add(f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="#000" stroke-width="{sw}"{d}/>')

    def poly(self, pts, dash=False, end=None):
        d = ' stroke-dasharray="6,4"' if dash else ""
        m = ""
        if end == "open":
            m = ' marker-end="url(#open)"'
        elif end == "diamond":
            m = ' marker-start="url(#diamond)"'
        p = " ".join(f"{x},{y}" for x, y in pts)
        self.add(f'<polyline points="{p}" fill="none" stroke="#000" stroke-width="1.2"{d}{m}/>')

    def save(self, name):
        defs = ('<defs><marker id="open" markerWidth="12" markerHeight="10" refX="11" refY="5" orient="auto">'
                '<polyline points="0,0 11,5 0,10" fill="none" stroke="#000" stroke-width="1.2"/></marker>'
                '<marker id="diamond" markerWidth="16" markerHeight="10" refX="1" refY="5" orient="auto">'
                '<polygon points="1,5 8,1 15,5 8,9" fill="#000"/></marker></defs>')
        svg = (f'<svg xmlns="http://www.w3.org/2000/svg" width="{self.w}" height="{self.h}" viewBox="0 0 {self.w} {self.h}">'
               f'<rect width="100%" height="100%" fill="#fff"/>{defs}{"".join(self.el)}</svg>')
        with open(os.path.join(OUT, name + ".svg"), "w", encoding="utf-8") as f:
            f.write(svg)


def stereo_box(s, x, y, w, h, stereo, lines, fill="#fff", bold=False, size=13):
    s.rect(x, y, w, h, fill=fill)
    s.text(x + w / 2, y + 16, f"«{stereo}»", size=11)
    for i, ln in enumerate(lines):
        s.text(x + w / 2, y + 35 + i * 18, ln, size=size, weight="bold" if bold and i == 0 else "normal")


# ---------------------------------------------------------------- use cases
def use_cases():
    s = Svg(915, 700)
    s.rect(165, 15, 570, 670, sw=1.6)
    s.text(185, 40, "«part» gameShelf : GameShelf", size=14, anchor="start")
    uc = {}
    W, H = 160, 58

    def u(key, x, y, l1, l2=""):
        stereo_box(s, x, y, W, H, "use case", [l1, l2] if l2 else [l1])
        uc[key] = (x, y)

    u("add", 180, 60, "Добавить игру")
    u("rawg", 410, 60, "Найти метаданные", "в RAWG")
    u("status", 180, 140, "Изменить статус", "прохождения")
    u("replay", 410, 140, "Начать повторное", "прохождение")
    u("session", 180, 220, "Учитывать игровую", "сессию")
    u("auto", 410, 220, "Отслеживать процесс", "игры")
    u("manual", 410, 295, "Скорректировать", "сессию вручную")
    u("backlog", 180, 375, "Просматривать", "бэклог")
    u("next", 410, 375, "Выбрать, во что", "поиграть")
    u("stats", 180, 455, "Просматривать", "статистику")
    u("export", 410, 455, "Экспортировать", "статистику CSV")
    u("goals", 180, 535, "Задать цели", "на год")
    u("backup", 180, 612, "Создать резервную", "копию")
    u("steam", 560, 535, "Импортировать", "библиотеку Steam")
    u("remind", 560, 612, "Напомнить о", "забытых играх")

    def inc(a, b, label="«include»", ext=False):
        ax, ay = uc[a]; bx, by = uc[b]
        s.poly([(ax + W, ay + H / 2), (bx, by + H / 2)], dash=True, end="open")
        s.text((ax + W + bx) / 2, (ay + by) / 2 + H / 2 - 7, "«extend»" if ext else label, size=12)

    inc("add", "rawg", ext=True)
    inc("status", "replay", ext=True)
    inc("session", "auto", ext=True)
    sx, sy = uc["session"]; mx, my = uc["manual"]
    s.poly([(sx + W, sy + H - 8), (mx, my + H / 2)], dash=True, end="open")
    s.text(330, 312, "«extend»", size=12, anchor="start")
    inc("backlog", "next", ext=True)
    inc("stats", "export")

    # actors
    def actor(x, y, name, sub=""):
        s.rect(x, y, 140, 58 if sub else 52, fill="#ececec")
        s.text(x + 70, y + 16, "«actor»", size=11)
        s.text(x + 70, y + 35, name, size=14, weight="bold")
        if sub:
            s.text(x + 70, y + 51, sub, size=12)
        return x, y

    ax, ay = actor(10, 300, "Player", "(игрок)")
    for k in ["add", "status", "session", "backlog", "stats", "goals", "backup", "steam"]:
        x, y = uc[k]
        if k == "steam":
            continue
        s.line(ax + 140, ay + 29, x, y + H / 2)
    # player -> steam import (через низ)
    s.poly([(ax + 70, ay + 58), (ax + 70, 700 - 25), (540, 675), (540, uc["steam"][1] + H / 2), (560, uc["steam"][1] + H / 2)])
    # external
    bx, by = actor(760, 515, "SteamWebAPI")
    s.line(720, uc["steam"][1] + H / 2, bx, by + 26)
    rx, ry = actor(760, 60, "RAWG API")
    s.line(570, 89, rx, ry + 26)
    ox, oy = actor(760, 220, "OperatingSystem", "(процессы ОС)")
    s.line(570, 249, ox, oy + 29)
    nx, ny = actor(760, 612, "OsNotifications")
    s.line(720, uc["remind"][1] + H / 2, nx, ny + 26)
    s.save("usecases")


# ---------------------------------------------------------------- hierarchy
def hierarchy(name, root, groups, width=760, idw=113):
    rowh, gap = 27, 16
    total = sum(len(g[2]) for g in groups) * rowh + gap * (len(groups) + 1)
    s = Svg(width, total + 10)
    gy = gap
    centers = []
    for gname, gdesc, items in groups:
        n = len(items)
        top = gy
        for i, (rid, title, new) in enumerate(items):
            y = top + i * rowh
            s.rect(370, y, width - 380, 22, fill=NEW if new else "#fff", stroke=NEW_STROKE if new else "#000")
            s.text(380, y + 16, rid, size=13, weight="bold", anchor="start")
            s.text(380 + idw, y + 16, "·  " + title, size=12.5, anchor="start")
            s.line(352, y + 11, 370, y + 11)
        s.line(352, top + 11, 352, top + (n - 1) * rowh + 11)
        cy = top + (n - 1) * rowh / 2 + 11
        bh = 56
        s.rect(160, cy - bh / 2, 175, bh, fill="#ececec")
        s.text(247, cy - bh / 2 + 15, "«requirement»", size=11)
        s.text(247, cy - bh / 2 + 33, gname, size=14, weight="bold")
        s.text(247, cy - bh / 2 + 49, gdesc, size=11)
        s.poly([(335, cy), (352, cy)])
        s.add(f'<polygon points="335,{cy} 342,{cy-4} 349,{cy} 342,{cy+4}" fill="#000"/>')
        centers.append(cy)
        gy += n * rowh + gap
    mid = (centers[0] + centers[-1]) / 2
    s.line(145, centers[0], 145, centers[-1])
    for c in centers:
        s.line(145, c, 160, c)
    s.rect(4, mid - 38, 122, 76, fill="#ececec")
    s.text(65, mid - 22, "«requirement»", size=11)
    s.text(65, mid - 4, root[0], size=14, weight="bold")
    for i, ln in enumerate(root[1:]):
        s.text(65, mid + 13 + i * 15, ln, size=11)
    s.line(126, mid, 145, mid)
    s.add(f'<polygon points="126,{mid} 133,{mid-4} 140,{mid} 133,{mid+4}" fill="#000"/>')
    s.save(name)


FUNC = [
    ("libraryGroup", "Библиотека игр", [
        ("REQ-FUNC-001", "Добавление игры", 0), ("REQ-FUNC-002", "Поиск метаданных в RAWG", 0),
        ("REQ-FUNC-003", "Импорт библиотеки Steam", 0), ("REQ-FUNC-004", "Редактирование и удаление", 0),
        ("REQ-FUNC-010", "Просмотр бэклога", 0), ("REQ-FUNC-011", "Карточка игры", 0),
        ("REQ-FUNC-018", "Платформы и коллекции", 0)]),
    ("progressGroup", "Статусы прохождения", [
        ("REQ-FUNC-005", "Статусы и переходы", 0), ("REQ-FUNC-019", "Повторное прохождение", 1),
        ("REQ-FUNC-020", "Напоминание о забытых играх", 1)]),
    ("timeGroup", "Учёт игрового времени", [
        ("REQ-FUNC-006", "Таймер игровой сессии", 0), ("REQ-FUNC-007", "Автоотслеживание процесса", 0),
        ("REQ-FUNC-008", "Ручная корректировка сессий", 0), ("REQ-FUNC-009", "Расчёт оставшегося времени", 0)]),
    ("statsGroup", "Статистика и цели", [
        ("REQ-FUNC-012", "Панель статистики", 0), ("REQ-FUNC-013", "Прогноз разбора бэклога", 0),
        ("REQ-FUNC-014", "Цели на год", 0), ("REQ-FUNC-015", "Выбор «что поиграть»", 0)]),
    ("dataGroup", "Данные и резервирование", [
        ("REQ-FUNC-016", "Резервная копия и восстановление", 0), ("REQ-FUNC-017", "Экспорт статистики CSV", 0)]),
]

NFR = [
    ("interfaces", "Внешние интерфейсы", [
        ("REQ-INT-001", "Steam Web API", 0), ("REQ-INT-002", "RAWG API", 0),
        ("REQ-INT-003", "Формат резервной копии JSON", 0), ("REQ-INT-004", "Формат экспорта CSV", 0)]),
    ("serviceQuality", "Качество обслуживания", [
        ("REQ-PERF-001", "Время запуска приложения", 0), ("REQ-PERF-002", "Фильтрация библиотеки", 0),
        ("REQ-PERF-003", "Построение статистики", 0), ("REQ-PERF-004", "Нагрузка автоотслеживания", 0),
        ("REQ-SEC-001", "Хранение ключа Steam API", 0), ("REQ-SEC-002", "Только HTTPS", 0),
        ("REQ-SEC-003", "Отсутствие телеметрии", 0), ("REQ-REL-001", "Сохранность активной сессии", 0),
        ("REQ-REL-002", "Целостность базы данных", 0), ("REQ-REL-003", "Работа без сети", 0),
        ("REQ-AVAIL-001", "Доступность функций офлайн", 0), ("REQ-OBS-001", "Журнал событий", 0)]),
    ("compliance", "Соответствие стандартам", [
        ("REQ-COMP-001", "Персональные данные", 0), ("REQ-COMP-002", "Условия использования API", 0)]),
    ("implementation", "Проектирование и реализация", [
        ("REQ-INST-001", "Установщик", 0), ("REQ-BUILD-001", "Сборка Maven и CI", 0),
        ("REQ-MAINT-001", "Модульность и тесты", 0), ("REQ-PORT-001", "Поддерживаемые ОС", 0),
        ("REQ-COST-001", "Стоимость эксплуатации", 0), ("REQ-DEAD-001", "Сроки итераций", 0),
        ("REQ-CM-001", "Управление изменениями", 0)]),
]


# ---------------------------------------------------------------- domain
def domain():
    s = Svg(780, 560)

    def blk(x, y, name, attrs, w=200, kind="part def"):
        h = 44 + 17 * len(attrs)
        s.rect(x, y, w, h, fill="#fff")
        s.text(x + w / 2, y + 15, f"«{kind}»", size=11)
        s.text(x + w / 2, y + 32, name, size=14, weight="bold")
        s.line(x, y + 40, x + w, y + 40)
        for i, a in enumerate(attrs):
            s.text(x + 8, y + 56 + i * 17, a, size=12, anchor="start")
        return (x, y, w, h)

    g = blk(280, 20, "Game", ["title : String", "status : GameStatus", "estimateHours : Real [0..1]",
                              "playedBeforeTracking : Duration", "rating : Integer [0..1]", "notes : String",
                              "executable : String [0..1]", "steamAppId : Integer [0..1]", "playthroughNo : Integer"], 230)
    p = blk(20, 40, "Platform", ["name : String", "store : String"], 180)
    t = blk(20, 200, "Collection", ["name : String"], 180)
    se = blk(570, 30, "Session", ["start : DateTime", "end : DateTime [0..1]", "source : SessionSource",
                                  "comment : String"], 185)
    sc = blk(570, 250, "StatusChange", ["from : GameStatus", "to : GameStatus", "at : DateTime",
                                        "playthroughNo : Integer"], 185)
    go = blk(20, 420, "YearGoal", ["year : Integer", "targetCompleted : Integer", "targetHours : Real [0..1]"], 220)
    st = blk(280, 420, "Settings", ["steamId : String", "steamKeyEnc : String", "idleMergeMin : Integer = 5",
                                    "reminderDays : Integer = 30"], 230)
    en = blk(560, 420, "GameStatus", ["BACKLOG, PLAYING,", "ON_HOLD, COMPLETED,", "COMPLETED_100, DROPPED"], 200, "enum def")

    def link(a, b, la, lb, pa, pb):
        s.line(*pa, *pb)
        s.text(pa[0] + (8 if pb[0] >= pa[0] else -8), pa[1] - 6, la, size=12, anchor="start" if pb[0] >= pa[0] else "end")
        s.text(pb[0] + (-8 if pb[0] >= pa[0] else 8), pb[1] - 6, lb, size=12, anchor="end" if pb[0] >= pa[0] else "start")

    link(g, p, "0..*", "1", (280, 90), (200, 90))
    link(g, t, "0..*", "0..*", (280, 200), (200, 225))
    # composition game -> session/statuschange
    s.poly([(510, 80), (570, 80)], end="diamond"); s.text(562, 74, "0..*", size=12, anchor="end")
    s.poly([(510, 190), (540, 190), (540, 290), (570, 290)], end="diamond"); s.text(562, 284, "0..*", size=12, anchor="end")
    s.save("domain")


if __name__ == "__main__":
    use_cases()
    hierarchy("func-req", ("gameShelfSpec", "Функциональные", "требования"), FUNC)
    hierarchy("nfr-req", ("qualitySpec", "Нефункциональные", "требования"), NFR, width=790, idw=128)
    domain()
    print("ok")
