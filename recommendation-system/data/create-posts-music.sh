#!/bin/bash

BASE_URL="http://localhost:8080"
USERNAME="user2"
PASSWORD="password123"

echo "=== Автоматическое создание 10 музыкальных постов ==="

export LANG=ru_RU.UTF-8
export LC_ALL=ru_RU.UTF-8

# ====================== ЛОГИН ======================
echo "Выполняется вход..."

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

echo "Успешный вход."

# ====================== СОЗДАНИЕ ПОСТОВ ======================
create_post() {
  local id=$1
  local text=$2

  # Создаём временный JSON файл (самый надёжный способ)
  cat > /tmp/post_$id.json << EOF
{
  "text": $text,
  "photoUrl": null
}
EOF

  RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X POST "$BASE_URL/posts" \
    -H "Content-Type: application/json; charset=utf-8" \
    -H "Authorization: Bearer $TOKEN" \
    --data-binary @/tmp/post_$id.json)

  if [ "$RESPONSE" = "201" ]; then
    echo "Пост #$id успешно создан"
  else
    echo "Ошибка при создании поста #$id (HTTP $RESPONSE)"
  fi

  rm -f /tmp/post_$id.json
}

echo "Начинаем создание постов..."

create_post 21 '"После того как я послушал Времена года Вивальди и 40-ю симфонию Моцарта вживую я окончательно влюбился в классику. Сейчас активно изучаю Баха особенно Хорошо темперированный клавир. Параллельно открыл для себя современную неоклассику Макс Рихтер и Olafur Arnalds."'

create_post 22 '"Неожиданно сильно зашел джаз. Последние две недели почти не выключаю Miles Davis особенно альбом Kind of Blue."'

create_post 23 '"Открыл для себя русский рэп нового поколения. Особенно сильно впечатлили Oxxxymiron Скриптонит и Баста."'

create_post 24 '"Электронная музыка сейчас очень цепляет. Много слушаю deep house и melodic techno."'

create_post 25 '"Вспомнил молодость и ностальгирую по року 70-80х. Led Zeppelin Pink Floyd Queen The Doors."'

create_post 26 '"Инди-рок и dream pop сейчас звучат очень хорошо. The Neighbourhood Cigarettes After Sex Arctic Monkeys."'

create_post 27 '"Погрузился в lo-fi hip hop. Отличный жанр для работы и учебы."'

create_post 28 '"Метал тоже в плейлисте. In Flames Dark Tranquillity Sabaton."'

create_post 29 '"Русская музыка отдельная любовь. Высоцкий Цой Кино ДДТ Наутилус Помпилиус."'

create_post 30 '"Сейчас много слушаю современный поп The Weeknd Billie Eilish Zivert МакSим."'

echo ""
echo "Создание постов завершено."
read -p "Нажмите Enter для закрытия окна..."