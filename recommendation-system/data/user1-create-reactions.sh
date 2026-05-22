#!/bin/bash

BASE_URL="http://localhost:8080"
USERNAME="user1"
PASSWORD="password123"

echo "=== user1: Ставим реакции на посты ==="

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

# ====================== ФУНКЦИЯ РЕАКЦИИ ======================
react() {
  local post_id=$1
  local reaction=$2

  RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X PUT "$BASE_URL/posts/$post_id/reactions" \
    -H "Content-Type: application/json; charset=utf-8" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"type\":\"$reaction\"}")

  if [ "$RESPONSE" = "200" ]; then
    echo "Реакция $reaction на пост #$post_id — OK"
  else
    echo "Ошибка реакции $reaction на пост #$post_id (HTTP $RESPONSE)"
  fi
}

echo "Ставим 10 LIKE на автомобильные посты (31-40)..."

for i in {31..40}; do
  react $i "LIKE"
done

echo ""
echo "Ставим 5 DISLIKE на музыкальные посты (21-25)..."

for i in {21..25}; do
  react $i "DISLIKE"
done

echo ""
echo "======================================"
echo "Все реакции успешно отправлены!"
read -p "Нажмите Enter для закрытия окна..."