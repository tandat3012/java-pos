# Cài và chạy môi trường POS Java

## Môi trường đã kiểm tra

Máy phát triển: CachyOS Linux x86_64, Zed, OpenJDK 21.0.12, Maven 3.9.16, Docker 29.7.2, Docker Compose 5.5.1. Các công cụ đã có trên máy, agent không cài phần mềm hệ thống.

## Cài lại trên CachyOS / Arch nếu cần

```sh
sudo pacman -Syu jdk21-openjdk maven
java -version
javac -version
mvn -version
```

Lệnh `-Syu` cập nhật hệ thống; xem danh sách pacman đưa ra trước khi đồng ý. Nếu có nhiều JDK:

```sh
archlinux-java status
sudo archlinux-java set java-21-openjdk
```

Nguồn: [Arch JDK 21](https://archlinux.org/packages/extra/x86_64/jdk21-openjdk/), [ArchWiki Java](https://wiki.archlinux.org/title/Java). Không cần Gradle, JavaFX hoặc Scene Builder.

## Build và chạy Swing (Linux / Windows)

Từ thư mục chứa `pom.xml`:

```sh
mvn clean verify
mvn compile exec:java
```

Maven tải dependency lần đầu. Ứng dụng hiện mở sáu màn hình mẫu An Nhiên POS; đóng cửa sổ để thoát. Swing chạy trên EDT, không cần database hoạt động để mở cửa sổ. JAR hiện là artifact Maven thông thường; dùng lệnh Maven ở trên để chạy, chưa làm installer hoặc JAR đóng gói kèm dependencies. Xem [tiến độ giao diện mẫu](UI_PROTOTYPE.md).

Trên Windows, cài JDK 21, đặt `JAVA_HOME` tới thư mục JDK, thêm thư mục `bin` của JDK và Maven vào PATH, mở terminal mới rồi kiểm tra `java -version`, `javac -version`, `mvn -version`. Chạy hai lệnh build/run trên bằng PowerShell tại project. Nguồn: [hướng dẫn cài Maven chính thức](https://maven.apache.org/install.html). Chưa kiểm chứng trên máy Windows.

## SQL Server local bằng Docker

Compose chọn `mcr.microsoft.com/mssql/server:2022-latest`, edition Developer, cho môi trường phát triển/demo. Docker host cần hỗ trợ image SQL Server Linux x86_64 và có đủ tài nguyên (tối thiểu 2 GB RAM cho SQL Server). Image chấp nhận EULA bằng `ACCEPT_EULA=Y`. Nguồn: [Microsoft SQL Server containers](https://learn.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker?view=sql-server-ver16), [triển khai container](https://learn.microsoft.com/en-us/sql/linux/containers/deploy?view=sql-server-ver16).

File `.local/sqlserver.env` đã được tạo trong lần setup này với mật khẩu ngẫu nhiên và quyền file 600. Không commit hoặc chia sẻ file đó. Khi lấy project trên máy khác, tạo thư mục `.local` và file `sqlserver.env` bằng editor, với nội dung sau rồi thay giá trị placeholder bằng mật khẩu mạnh của bạn:

```properties
MSSQL_SA_PASSWORD=<mat-khau-local-cua-ban>
```

Mật khẩu SQL Server cần ít nhất 8 ký tự, có ít nhất 3 nhóm trong chữ hoa, chữ thường, số và ký hiệu. Không chạy với chuỗi placeholder ở trên. File này chỉ cấp biến cho Compose, không thêm thư viện .env vào Java. Trên Linux đặt quyền riêng tư:

```sh
chmod 700 .local
chmod 600 .local/sqlserver.env
```

Khởi chạy và kiểm tra:

```sh
docker compose --env-file .local/sqlserver.env up -d --wait
docker compose --env-file .local/sqlserver.env ps
```

- Host: `localhost`, port: `5436`; ánh xạ thực tế `127.0.0.1:5436:1433`.
- Tài khoản quản trị local: `sa`; mật khẩu nằm trong file local ở trên.
- Healthcheck chạy `SELECT 1`; không tạo database, schema hoặc dữ liệu nghiệp vụ.
- Volume `java-pos_sqlserver-data` giữ dữ liệu khi container được tạo lại.
- Tag `2022-latest` có thể được cập nhật bản vá; digest image chạy thực tế ghi ở setup notes.

Dừng database khi không dùng:

```sh
docker compose --env-file .local/sqlserver.env stop
```

Không xóa volume để dừng thông thường. Khi đã có volume, thay biến mật khẩu không tự đổi mật khẩu SQL Server đang lưu; giữ file credentials tương ứng với volume.

## Cấu hình Java cho giai đoạn sau

Sao chép `src/main/resources/config/database.properties.example` thành `database.properties` cùng thư mục nếu cần điền cấu hình local. File thật được ignore và loại khỏi JAR để không đóng gói mật khẩu. `DatabaseConnection` hiện chưa nạp cấu hình; tích hợp sẽ thực hiện ở bước sau.

JDBC URL dự kiến cho database ứng dụng sau khi được thiết kế/tạo:

```text
jdbc:sqlserver://localhost:5436;databaseName=<database-name>;encrypt=true;trustServerCertificate=true
```

`trustServerCertificate=true` chỉ dùng cho container phát triển local với chứng chỉ tự ký. Khi demo với SQL Server trên Windows, cập nhật host/port/database và cấu hình chứng chỉ phù hợp với máy đích; không đổi sang driver MySQL/PostgreSQL.

Driver đã chọn: `com.microsoft.sqlserver:mssql-jdbc:13.4.0.jre11`, hỗ trợ Java 21 theo [Microsoft](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver17). POI/JasperReports chưa được thêm.
