# Setup notes

> Trạng thái setup dưới đây là lịch sử. Theo yêu cầu mới, project đã chuyển sang **giao diện mẫu dùng dữ liệu trong bộ nhớ**. Xem [UI_PROTOTYPE.md](UI_PROTOTYPE.md) cho sáu màn hình, kiểm chứng render và các phần còn thiếu. POM hiện có thêm Apache POI 5.3.0 và chạy ứng dụng bằng `mvn compile exec:java`.

## Trạng thái hiện tại — 2026-09-07

```text
PROJECT PHASE: SETUP
JAVA BUILD: PASSED
SWING SKELETON: PASSED
DOCKER CONFIG: VALIDATED
DATABASE RUNTIME: IMAGE PULL IN PROGRESS
BUSINESS LOGIC: NOT STARTED
DATABASE SCHEMA: NOT STARTED
UI IMPLEMENTATION: PLACEHOLDER ONLY
REPORTING: NOT STARTED
```

Đã hoàn thiện cấu trúc Java/Maven và chạy được cửa sổ Swing. SQL Server Docker đang tải image, chưa xác minh container healthy hoặc kết nối JDBC. Không coi việc thêm dependency là đã kết nối database.

## Lựa chọn đã xác nhận

Nguồn: [SETUP_DECISIONS.md](SETUP_DECISIONS.md) và xác nhận bổ sung trong hội thoại.

| Hạng mục | Lựa chọn |
| --- | --- |
| Java | 21 |
| UI | Swing; được phép tạo cửa sổ mẫu |
| Database | Microsoft SQL Server |
| Build tool | Maven |
| IDE/editor | Zed |
| Runtime phát triển | Docker local; SQL Server trong container |
| Database version | Agent chọn 2022 Developer theo quyền chọn version tương thích |
| Cổng | Host 5436 → SQL Server 1433; bind 127.0.0.1 |
| Cấu hình Java | File .properties local |
| Môi trường | Linux phát triển, Windows demo |
| POI | Để sau |
| JasperReports | Người dùng ghi “Để”; giữ để sau, chưa thêm |
| JavaFX / Scene Builder / thư viện .env Java | Không áp dụng |

Không còn quyết định stack nào chặn build. Database name/schema và triển khai đọc cấu hình/kết nối thuộc giai đoạn tiếp theo. Windows demo chưa được kiểm chứng.

## Công cụ / dependency

Mọi thành phần thêm hoặc sử dụng được ghi bên dưới. Các version default lifecycle Maven là bản đã quan sát trong build; hai plugin cấu hình trực tiếp và JDBC được pin trong POM.

| Thành phần | Mục đích | Version | Cách cài | File cấu hình liên quan | Trạng thái |
| --- | --- | --- | --- | --- | --- |
| JDK | Compile/run Java | OpenJDK 21.0.12+8 | Đã có trên PATH; agent không cài | pom.xml (release 21) | Đã kiểm tra |
| Maven | Build/dependency | 3.9.16 | Đã có trên PATH; agent không cài | pom.xml | Build thành công |
| Swing | Desktop UI | Theo JDK 21 | Có sẵn trong JDK, không thêm dependency | Main.java | Chạy thành công |
| Microsoft JDBC | SQL Server JDBC | 13.4.0.jre11 | Maven Central qua dependency trong POM | pom.xml | Đã resolve; runtime connection chưa kiểm tra |
| Maven Compiler Plugin | Compile target Java 21 | 3.15.0 | Maven resolve | pom.xml | Đã chạy |
| Exec Maven Plugin | Chạy ứng dụng bằng JVM riêng | 3.6.3 | Maven resolve | pom.xml | Đã chạy |
| Maven lifecycle plugins | Clean/resources/test/JAR mặc định | clean 3.2.0, resources 3.4.0, surefire 3.5.4, jar 3.5.0 | Maven resolve mặc định | Default lifecycle Maven | Đã chạy; không có test nghiệp vụ |
| Docker | Database runtime | 29.7.2 client/server | Có sẵn, agent không cài | compose.yaml | Truy cập được ngoài sandbox |
| Docker Compose | Quản lý container local | 5.5.1 | Có sẵn | compose.yaml | Config hợp lệ; đang pull image |
| SQL Server | Database local cùng loại khi demo | 2022 Developer, image 2022-latest | docker compose up --wait | compose.yaml | Image đang tải, chưa xác minh runtime |
| Apache POI | Excel giai đoạn sau | Chưa chọn | Chưa cài | Chưa thêm vào POM | Deferred |
| JasperReports | Hóa đơn giai đoạn sau | Chưa chọn | Chưa cài | Chưa thêm vào POM | Deferred |
| Zed | Editor | Không kiểm tra version | Người dùng đang sử dụng | Không thêm cấu hình IDE | Đã xác nhận |

Image digest SQL Server: Pending — bổ sung sau khi pull hoàn tất. Tag 2022-latest có thể nhận bản vá mới.

Nguồn tra cứu: [Microsoft JDBC](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver17), [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/plugin-info.html), [Exec Maven Plugin](https://www.mojohaus.org/exec-maven-plugin/exec-mojo.html), [SQL Server containers](https://learn.microsoft.com/en-us/sql/linux/containers/deploy?view=sql-server-ver16). Hướng dẫn cài hệ điều hành có tại [INSTALLATION.md](INSTALLATION.md); chưa thực hiện cài phần mềm hệ thống.

## File / thành phần skeleton đã thêm

- Thành phần: Maven và Java skeleton.
  - Mục đích: Build Java 21, chạy cửa sổ Swing trên EDT.
  - Version: Project 0.1.0-SNAPSHOT; Java 21.
  - Cách cài: Tạo pom.xml, Main và các thư mục skeleton.
  - File cấu hình liên quan: pom.xml, src/main/java/pos/, src/test/java/.
  - Trạng thái: Build/run đã kiểm chứng. DatabaseConnection vẫn chỉ là skeleton. Không có framework, ORM hoặc nghiệp vụ.
- Thành phần: Resources/config example.
  - Mục đích: Chuẩn bị properties local và chỗ chứa tài nguyên sau này.
  - Version: N/A.
  - Cách cài: Tạo file ví dụ rỗng, thư mục css/reports/config; bỏ FXML do chọn Swing.
  - File cấu hình liên quan: src/main/resources/config/database.properties.example, pom.xml.
  - Trạng thái: Chưa được ứng dụng đọc. File database.properties thật bị loại khỏi resources/JAR.
- Thành phần: SQL Server Compose và credentials local.
  - Mục đích: Host SQL Server local, lưu dữ liệu trong named volume, healthcheck SELECT 1.
  - Version: 2022-latest.
  - Cách cài: Hoàn thiện compose.yaml vốn là file rỗng; sinh mật khẩu ngẫu nhiên vào .local/sqlserver.env với quyền 600.
  - File cấu hình liên quan: compose.yaml, .local/sqlserver.env (ignored), .gitignore.
  - Trạng thái: Config đã validate; image đang tải. Không tạo schema hoặc database ứng dụng. Không đưa mật khẩu vào source/log/tài liệu. File env chỉ dành cho Compose, không dùng thư viện .env trong Java.
- Thành phần: Tài liệu và .gitignore.
  - Mục đích: Ghi stack, cách chạy Linux/Windows, bảo vệ credentials/artifacts và theo dõi setup.
  - Version: N/A.
  - Cách cài: Tạo/cập nhật file văn bản.
  - File cấu hình liên quan: README.md, docs/, POS_JAVA_SETUP_AGENT_INSTRUCTIONS.md, .gitignore.
  - Trạng thái: Đã cập nhật.

## Checklist

- [x] Xác nhận Java version (21)
- [x] Xác nhận Swing hoặc JavaFX (Swing)
- [x] Xác nhận MySQL hoặc SQL Server (SQL Server)
- [x] Xác nhận Maven hoặc Gradle (Maven)
- [x] Xác nhận IDE/editor (Zed)
- [x] Xác nhận database chạy local hay Docker (Docker)
- [x] Khởi tạo project và cấu hình Maven
- [x] Tạo package structure
- [x] Tạo resources structure
- [x] Tạo .gitignore
- [x] Tạo README
- [x] Tạo SETUP_NOTES.md
- [x] Thêm JDBC driver
- [x] Thêm UI dependency nếu cần (Swing có sẵn trong JDK)
- [ ] Thêm Apache POI nếu muốn setup ngay — Deferred theo lựa chọn
- [ ] Thêm JasperReports nếu muốn setup ngay — Deferred
- [x] Tạo database config placeholder
- [x] Tạo DatabaseConnection skeleton
- [x] Tạo Main application skeleton (cửa sổ Swing trên EDT)
- [x] Build project thành công
- [x] Chạy application skeleton thành công
- [x] Tạo và validate Docker Compose
- [ ] Khởi chạy SQL Server Docker healthy
- [ ] Kiểm tra JDBC tới SQL Server local

## Kiểm chứng thực tế

Các command shell được chạy qua proxy `rtk` theo hướng dẫn môi trường. Dùng `-Dmaven.repo.local=/tmp/java-pos-m2` để lưu Maven cache kiểm tra trong /tmp. Người dùng có thể dùng cache Maven mặc định với lệnh ngắn trong README.

| Kiểm tra | Kết quả |
| --- | --- |
| java -version / javac -version / mvn -version | Java 21.0.12, Maven 3.9.16 |
| mvn -B -ntp -Dmaven.repo.local=/tmp/java-pos-m2 clean verify | BUILD SUCCESS; 2 source Java, release 21 |
| mvn -B -ntp -Dmaven.repo.local=/tmp/java-pos-m2 compile exec:exec | Cửa sổ chạy, tiến trình kết thúc bình thường; BUILD SUCCESS |
| javac --release 21 -cp target/classes -d /tmp/java-pos-smoke /tmp/java-pos-smoke/SetupSmoke.java | Harness kiểm tra tạm compile thành công |
| java -cp target/classes:/tmp/java-pos-smoke SetupSmoke ui | PASS: Main tạo cửa sổ hiển thị; kiểm tra trên EDT rồi dispose |
| Kiểm tra ảnh /tmp/java-pos-smoke/window.png | Hiển thị đúng thông báo tiếng Việt trong cửa sổ mẫu |
| Kiểm tra nội dung target/pos-java-0.1.0-SNAPSHOT.jar | Có Main.class và example; không có credentials local hoặc .gitkeep |
| docker compose --env-file .local/sqlserver.env config --quiet | Exit 0 |
| docker compose --env-file .local/sqlserver.env up -d --wait --wait-timeout 180 | Đang pull image; chưa có kết quả healthy |

Không thêm dependency test hay unit test nghiệp vụ. Harness ở /tmp chỉ xác minh cửa sổ và chuẩn bị kiểm tra JDBC, không nằm trong project. Maven verify không có test nghiệp vụ để chạy.

## Lịch sử lỗi môi trường đã xử lý

- Ban đầu chưa tìm thấy JDK/Maven; lần kiểm tra mới đã có trên PATH.
- Maven trong sandbox không resolve được DNS Maven Central; chạy ngoài sandbox đã tải và build thành công.
- Docker socket và X11 bị chặn trong sandbox; chạy ngoài sandbox truy cập được daemon và kiểm tra được cửa sổ.
- Git status ban đầu báo không phải Git repository; metadata .git do môi trường quản lý, không sửa hoặc khởi tạo lại.

Phần còn chờ hiện tại là image SQL Server tải xong và kiểm tra runtime/JDBC. Chưa kiểm chứng Windows và chưa triển khai nghiệp vụ; các phần đó không được đánh dấu hoàn tất.


## Giới hạn tải image và kết quả review

- Manifest SQL Server có tổng dung lượng layer nén 624.870.837 byte (khoảng 625 MB).
- Probe trực tiếp 1 MB từ blob registry Microsoft nhận khoảng 606 KB trong 20 giây rồi hết thời gian (khoảng 30 KB/s). Tốc độ tải từ registry là giới hạn hiện tại; không phải lỗi build hoặc lỗi Compose.
- Lần kiểm tra Docker Compose ps gần nhất chưa có container của project. Lệnh up --wait vẫn đang chạy để tải image và sẽ thử khởi động sau khi tải xong. Không ghi nhận SQL Server healthy hoặc JDBC thành công khi chưa có bằng chứng.
- Reviewer độc lập xác minh Compose hợp lệ, JAR không chứa credentials và Main dùng bytecode Java 21/EDT; không phát hiện lỗi code/config cần sửa. Đánh giá toàn bộ setup còn thiếu bằng chứng runtime/JDBC, chưa PASS hoàn toàn.
- Khi image tải xong, kiểm tra lại bằng các lệnh Docker trong INSTALLATION.md và xác minh JDBC trước khi đánh dấu hai mục runtime cuối checklist. Nếu tiến trình tải dừng, chạy lại lệnh up --wait; không tạo container khác hoặc xóa volume.
