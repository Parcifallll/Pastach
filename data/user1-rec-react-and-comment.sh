#!/bin/bash

BASE_URL="http://localhost:8080"
USERNAME="user1"
PASSWORD="password123"

echo "=== user1: Реакции и комментарии на рекомендованные посты ==="

export LANG=ru_RU.UTF-8
export LC_ALL=ru_RU.UTF-8

# ====================== ЛОГИН ======================
echo "Выполняется вход под user1..."

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d':' -f2 | tr -d '"')

if [ -z "$TOKEN" ]; then
  echo "Ошибка авторизации!"
  echo "$LOGIN_RESPONSE"
  read -p "Нажмите Enter для выхода..."
  exit 1
fi

echo "Успешный вход под user1."

# ====================== ФУНКЦИИ ======================
react() {
  local post_id=$1
  local reaction=$2

  RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X PUT "$BASE_URL/posts/$post_id/reactions" \
    -H "Content-Type: application/json; charset=utf-8" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"type\":\"$reaction\"}")

  if [ "$RESPONSE" = "200" ]; then
    echo "  Реакция $reaction на пост #$post_id — OK"
  else
    echo "  Ошибка реакции $reaction на пост #$post_id (HTTP $RESPONSE)"
  fi
}

comment() {
  local post_id=$1
  local text=$2

  RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X POST "$BASE_URL/posts/$post_id/comments" \
    -H "Content-Type: application/json; charset=utf-8" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"text\":\"$text\"}")

  if [ "$RESPONSE" = "200" ]; then
    echo "  Комментарий на пост #$post_id — OK"
  else
    echo "  Ошибка комментария на пост #$post_id (HTTP $RESPONSE)"
  fi
}

# =============================================================
# ПРИРОДА — посты 22 25 28 29 30 31 33 34 35 43 47 52 53 55 56
#            60 61 64 65 67 80 81 83 90 91 97
# user1 любит природу — лайки, тёплые комментарии
# ~70% постов получают реакцию, ~70% — комментарий
# =============================================================
echo ""
echo "--- ПРИРОДА: реакции и комментарии ---"

# Only reaction
react 22 "LIKE"
react 28 "LIKE"
react 33 "LIKE"
react 43 "LIKE"
react 56 "LIKE"
react 64 "LIKE"
react 81 "LIKE"

# Only comment
comment 25 "Час смотреть на паука — это настоящая медитация."
comment 29 "У нас в лесу то же самое, утром пусто — вечером целая поляна!"
comment 34 "Родниковая вода — лучшее что есть в природе."
comment 47 "Туман на реке — это что-то особенное, очень атмосферно."
comment 60 "Осьминоги невероятные существа, менять цвет прямо на глазах — фантастика."
comment 80 "Подводный мир даже с поверхности завораживает."
comment 91 "Запах мокрой земли и грибов в лесу — незабываемо."

# Both reaction and comment
react 30 "LIKE"
comment 30 "Черёмуха в цвету — один из лучших запахов весны."

react 31 "LIKE"
comment 31 "Горные озёра с прозрачной водой — мечта."

react 35 "LIKE"
comment 35 "Ежи такие самостоятельные, мне нравится."

react 52 "LIKE"
comment 52 "Лебеди в городе всегда неожиданно и красиво."

react 53 "LIKE"
comment 53 "Фосфоресцирующее море — видел однажды, не забуду никогда. Как будто из другого мира."

react 55 "LIKE"
comment 55 "Через цветущий луг на велосипеде — это счастье."

react 65 "LIKE"
comment 65 "Иней на ветках правда превращает всё в сказку."

react 67 "LIKE"
comment 67 "Два пейзажа в одном — реальный и отражение. Очень поэтично."

react 83 "LIKE"
comment 83 "Ранним утром на берегу моря — лучшее время суток."

react 97 "LIKE"
comment 97 "Байдарка по тихой реке — идеальный отдых. Надо попробовать этим летом."

# Skipped (no reaction, no comment): 61 90 — просто промотал

# =============================================================
# АВТОМОБИЛИ LIKE — посты 124 125 126 128 131 135 144 145
#                        151 158 162 167 172 180 189 190
# user1 любит авто — лайки, живые комментарии
# =============================================================
echo ""
echo "--- АВТОМОБИЛИ (интересные): реакции и комментарии ---"

# Only reaction
react 124 "LIKE"
react 126 "LIKE"
react 144 "LIKE"
react 158 "LIKE"
react 172 "LIKE"
react 189 "LIKE"

# Only comment
comment 125 "Автопилот Tesla реально меняет отношение к дальним поездкам."
comment 128 "Mazda 6 — абсолютно согласен, незаслуженно недооценённая машина."
comment 145 "S-класс сзади — это отдельный вид удовольствия."
comment 162 "Иногда просто взять и поехать — лучшее решение."
comment 180 "Серпантин на заднеприводном купе — это адреналин в чистом виде."

# Both reaction and comment
react 131 "LIKE"
comment 131 "Шумоизоляция дверей — первое что делаю на новой машине. Разница колоссальная."

react 135 "LIKE"
comment 135 "Patrol vs Land Cruiser — вечный спор. Оба легенды."

react 151 "LIKE"
comment 151 "Полировка своими руками — долго, но результат того стоит. Сам делал, знаю."

react 167 "LIKE"
comment 167 "Tucson нового поколения реально подрос в классе, интерьер приятно удивляет."

react 190 "LIKE"
comment 190 "Универсал — это практично. Пересел с седана и не жалею."

# Skipped (no reaction, no comment): 162 — уже прокомментирован выше

# =============================================================
# АВТОМОБИЛИ нейтрал — посты 133 138 147 154 184 192 198
# технические посты — часть дизлайкнул, часть просто пролистал
# =============================================================
echo ""
echo "--- АВТОМОБИЛИ (технические): реакции ---"

# Only reaction — дизлайки на скучные посты про обслуживание
react 138 "DISLIKE"
react 147 "DISLIKE"
react 192 "DISLIKE"

# Only comment — нейтральные короткие
comment 133 "Масло — да, важная тема."
comment 154 "Ремень ГРМ — это святое, не откладывать."

# Skipped: 184 198 — просто проскроллил

# =============================================================
# МУЗЫКА — пост 236
# user1 музыку не любит — дизлайк, без комментария
# =============================================================
echo ""
echo "--- МУЗЫКА: реакция ---"

react 236 "DISLIKE"

echo ""
echo "=== Скрипт завершён (реакции + комментарии) ==="
read -p "Нажмите Enter для закрытия окна..."