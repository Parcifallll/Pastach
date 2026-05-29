#!/bin/bash

BASE_URL="http://localhost:8080"
USERNAME="user1"
PASSWORD="password123"

USER_ID=1

echo "=== user1: Просмотры рекомендованных постов ==="

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

view_post() {
  local post_id=$1
  local view_duration=$2
  local viewed_at
  viewed_at=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

  RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X POST "$BASE_URL/posts/recommendations/$post_id/view" \
    -H "Content-Type: application/json; charset=utf-8" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"viewedAt\":\"$viewed_at\",\"viewDuration\":$view_duration}")

  if [ "$RESPONSE" = "204" ]; then
    echo "  Просмотр поста #$post_id (${view_duration}s) — OK"
  else
    echo "  Ошибка просмотра поста #$post_id (HTTP $RESPONSE)"
  fi
}

# =============================================================
# ПРИРОДА: рекомендованные посты (конкретные ID из ответа API)
# user1 любит природу — большинство постов смотрит долго
# =============================================================
echo ""
echo "--- ПРИРОДА: просматриваем рекомендованные посты ---"

# LIKE-duration (30–120s) — интересные посты про природу, животных, пейзажи
for post_id in 22 25 28 29 30 31 33 34 35 43 47 52 53 55 56 60 61 64 65 67 80 81 83 90 91 97; do
  duration=$(( RANDOM % 91 + 30 )) # interesting nature post — long view
  view_post $post_id $duration
  sleep 0.2
done

# =============================================================
# АВТОМОБИЛИ: рекомендованные посты
# user1 любит автомобили — большинство смотрит долго,
# скучные технические (масло, фильтр, ремень) — чуть покороче
# =============================================================
echo ""
echo "--- АВТОМОБИЛИ: просматриваем рекомендованные посты ---"

# LIKE-duration (30–120s) — интересные авто-посты (марки, тюнинг, впечатления)
for post_id in 124 125 126 128 131 135 144 145 151 158 162 167 172 180 189 190; do
  duration=$(( RANDOM % 91 + 30 )) # interesting car post — long view
  view_post $post_id $duration
  sleep 0.2
done

# Neutral-duration (10–25s) — скучноватые технические посты (расходники, обслуживание)
for post_id in 133 138 147 154 184 192 198; do
  duration=$(( RANDOM % 16 + 10 )) # boring maintenance post — short-medium view
  view_post $post_id $duration
  sleep 0.2
done

# =============================================================
# МУЗЫКА: рекомендованные посты
# user1 музыку не любит — смотрит мельком
# =============================================================
echo ""
echo "--- МУЗЫКА: просматриваем рекомендованные посты ---"

# DISLIKE-duration (3–12s) — неинтересная тема, пролистывает быстро
for post_id in 236; do
  duration=$(( RANDOM % 10 + 3 )) # uninteresting music post — very short view
  view_post $post_id $duration
  sleep 0.2
done

echo ""
echo "=== Скрипт завершён ==="
read -p "Нажмите Enter для закрытия окна..."