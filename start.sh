#!/bin/bash
echo "检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "❌ Java未安装，请先安装Java 8+"
    exit 1
fi

echo "✅ Java环境检查通过: $(java -version 2>&1 | head -n 1)"

echo "检查Maven环境..."
if command -v mvn &> /dev/null; then
    echo "✅ Maven已安装: $(mvn -version | head -n 1)"
    echo "使用Maven启动应用程序..."
    mvn spring-boot:run
else
    echo "⚠️  Maven未安装，尝试使用Gradle或直接运行JAR..."
    echo "请安装Maven后再试，或者使用以下方式构建项目："
    echo "1. 下载并安装Maven: https://maven.apache.org/install.html"
    echo "2. 或者使用IDE导入项目进行编译运行"
    echo ""
    echo "如果已安装但未识别，请确保Maven在PATH环境中："
    echo "export PATH=/path/to/maven/bin:\$PATH"
fi