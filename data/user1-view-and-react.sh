#!/bin/bash

BASE_URL="http://localhost:8080"
USERNAME="user1"
PASSWORD="password123"

echo "=== user1: Просмотры и реакции на посты ==="

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

# =============================================================
# ПРИРОДА: посты 1–20
# 10 LIKE (посты 1–10), 10 DISLIKE (посты 11–20)
# =============================================================
echo ""
echo "--- ПРИРОДА: просматриваем посты 1–20 ---"

for i in $(seq 1 20); do
  duration=$(( RANDOM % 91 + 30 ))
  view_post $i $duration
  sleep 0.2
done

echo ""
echo "--- ПРИРОДА: реакции (1–10 LIKE, 11–20 DISLIKE) ---"

for i in $(seq 1 10); do
  react $i "LIKE"
  sleep 0.1
done

for i in $(seq 11 20); do
  react $i "DISLIKE"
  sleep 0.1
done

# =============================================================
# АВТОМОБИЛИ: посты 101–120
# 18 LIKE (посты 101–118), 2 DISLIKE (посты 119–120)
# =============================================================
echo ""
echo "--- АВТОМОБИЛИ: просматриваем посты 101–120 ---"

for i in $(seq 101 120); do
  duration=$(( RANDOM % 91 + 30 ))
  view_post $i $duration
  sleep 0.2
done

echo ""
echo "--- АВТОМОБИЛИ: реакции (101–118 LIKE, 119–120 DISLIKE) ---"

for i in $(seq 101 118); do
  react $i "LIKE"
  sleep 0.1
done

for i in $(seq 119 120); do
  react $i "DISLIKE"
  sleep 0.1
done

# =============================================================
# МУЗЫКА: посты 201–220
# 5 LIKE (посты 201–205), 15 DISLIKE (посты 206–220)
# =============================================================
echo ""
echo "--- МУЗЫКА: просматриваем посты 201–220 ---"

for i in $(seq 201 220); do
  duration=$(( RANDOM % 91 + 30 ))
  view_post $i $duration
  sleep 0.2
done

echo ""
echo "--- МУЗЫКА: реакции (201–205 LIKE, 206–220 DISLIKE) ---"

for i in $(seq 201 205); do
  react $i "LIKE"
  sleep 0.1
done

for i in $(seq 206 220); do
  react $i "DISLIKE"
  sleep 0.1
done

echo ""
read -p "Нажмите Enter для закрытия окна..."