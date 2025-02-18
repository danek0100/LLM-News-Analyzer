import csv
import json
import random
import time
import os
from kafka import KafkaProducer
from datetime import datetime

# Чтение конфигурации топика из окружения
kafka_topic = os.getenv('KAFKA_TOPIC', 'raw-news')
kafka_bootstrap_servers = os.getenv('KAFKA_HOST', 'localhost') + ':' + os.getenv('KAFKA_PORT', '9099')

# Создание Kafka producer
producer = KafkaProducer(
    bootstrap_servers=kafka_bootstrap_servers,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# Функция для отправки сообщений в Kafka
def send_to_kafka(title, published_at):
    message = {"title": title, "published_at": published_at}
    producer.send(kafka_topic, key=title, value=message)  # Заголовок как ключ
    print(f"Отправлено сообщение с ключом: {title}, сообщение: {message}")

# Чтение данных из CSV
with open('./archive/data.csv', newline='', encoding='utf-8') as csvfile:
    reader = csv.DictReader(csvfile)

    # Процесс отправки новостей в Kafka с случайной интенсивностью
    for row in reader:
        title = row['headline']
        published_at = row['published']

        # Преобразуем строку даты в объект datetime для корректного формата
        try:
            published_at = datetime.strptime(published_at, "%Y-%m-%d %H:%M:%S")
            published_at = published_at.isoformat()  # Преобразуем в строку ISO 8601
        except ValueError:
            print(f"Ошибка преобразования даты для {title}")
            continue

        send_to_kafka(title, published_at)

        # Задержка между отправками для случайной интенсивности
        time.sleep(random.uniform(1, 10))  # случайная задержка от 0.5 до 2 секунд
