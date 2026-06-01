#!/bin/bash

echo "Searching for Java 17+..."

# 1. Проверяем, есть ли JAVA_HOME
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    echo "Using JAVA_HOME: $JAVA_HOME"
else
    # 2. Пытаемся найти Java автоматически
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge 17 ]; then
            echo "Found system Java: version $JAVA_VERSION"
        else
            echo "[ERROR] System Java is too old (found $JAVA_VERSION, need 17+)."
            exit 1
        fi
    else
        echo "[ERROR] Java not found."
        echo "Please install JDK 17 or higher:"
        echo "sudo apt install openjdk-17-jdk"
        exit 1
    fi
fi

# 3. Запуск
echo ""
echo "Starting KanbaNice Desktop..."
echo "Make sure the backend is running on http://localhost:8080"
echo ""

# Делаем mvnw исполняемым, если нет
chmod +x ./mvnw

# Запуск через Maven wrapper
./mvnw javafx:run

echo ""
echo "Application closed."