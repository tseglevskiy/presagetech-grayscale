#!/usr/bin/env bash

set -euo pipefail
cd "$(dirname "$0")"

BUILD_DIR="build"

echo "Cleaning previous build..."
rm -rf "$BUILD_DIR"

echo "🔧 Configuring project with tests enabled..."
cmake -S . -B "$BUILD_DIR" -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTING=ON

echo "🛠️  Building project and tests..."
cmake --build "$BUILD_DIR" --parallel

echo "✅ Running tests..."
ctest --output-on-failure --test-dir "$BUILD_DIR"
