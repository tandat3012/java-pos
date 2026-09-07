# POS Java Desktop — AI Agent Setup Instructions

## 1. Mục tiêu của tài liệu

Tài liệu này dùng để hướng dẫn AI Agent **chỉ thực hiện bước setup ban đầu** cho đồ án POS Java Desktop.

Phạm vi hiện tại:

- Khởi tạo cấu trúc project.
- Chuẩn bị cấu hình build/dependency.
- Chuẩn bị cấu trúc thư mục.
- Ghi chú các công nghệ/thư viện cần cài đặt.
- Ghi chú các quyết định kỹ thuật còn chờ người dùng xác nhận.
- Không triển khai nghiệp vụ.
- Không tạo database schema.
- Không viết CRUD.
- Không làm giao diện chức năng.
- Không tạo báo cáo, hóa đơn, Excel hoặc JasperReports template.
- Không tự quyết định các lựa chọn chưa được chốt.

AI Agent phải ưu tiên **setup tối thiểu, rõ ràng, dễ thay đổi về sau**.

---

## 2. Yêu cầu kỹ thuật của đề tài

Đề tài yêu cầu:

- Ngôn ngữ: Java.
- Loại ứng dụng: Desktop Application.
- Giao diện: Java Swing hoặc JavaFX.
- Database: MySQL hoặc Microsoft SQL Server.
- Kết nối database: JDBC.
- Xuất Excel: Apache POI.
- In/xuất hóa đơn: JasperReports.

Các lựa chọn sau **chưa được mặc định xem là đã chốt**:

- Swing hay JavaFX.
- MySQL hay Microsoft SQL Server.
- Java version.
- Maven hay Gradle.
- IDE sử dụng.
- Có dùng Docker cho database hay không.
- Có dùng Scene Builder nếu chọn JavaFX hay không.
- Version cụ thể của Apache POI.
- Version cụ thể của JasperReports.
- Version JDBC driver.
- Cách quản lý biến môi trường / database credentials.

AI Agent phải ghi chú các mục này thay vì tự ý chọn.

---

## 3. Nguyên tắc setup

AI Agent phải tuân thủ:

1. Không thêm framework backend như Spring Boot.
2. Không thêm ORM như Hibernate/JPA.
3. Không thêm frontend web như React, Angular, Vue hoặc Next.js.
4. Không triển khai authentication.
5. Không triển khai business logic.
6. Không tự sinh database schema nếu chưa được yêu cầu.
7. Không thêm dependency ngoài những dependency cần thiết cho setup.
8. Không tạo abstraction quá phức tạp.
9. Không áp dụng Clean Architecture hoặc DDD quá mức cho đồ án nhỏ.
10. Mọi dependency hoặc công cụ được thêm phải được ghi lại trong file setup notes.
11. Nếu có nhiều lựa chọn hợp lệ, AI Agent phải ghi chú để người dùng quyết định.
12. Không tự thay đổi lựa chọn kỹ thuật đã được người dùng xác nhận trước đó.

---

## 4. Cấu trúc project dự kiến

Chỉ tạo skeleton, chưa triển khai chức năng.

```text
pos-java/
├── README.md
├── pom.xml hoặc build.gradle
│
├── docs/
│   └── SETUP_NOTES.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── <base-package>/
│   │   │       ├── Main.java
│   │   │       ├── config/
│   │   │       ├── model/
│   │   │       ├── dao/
│   │   │       ├── service/
│   │   │       ├── controller/
│   │   │       └── view/
│   │   │
│   │   └── resources/
│   │       ├── css/
│   │       ├── fxml/
│   │       ├── reports/
│   │       └── config/
│   │
│   └── test/
│       └── java/
│
└── .gitignore
```

Lưu ý:

- Nếu chọn Swing thì thư mục `fxml/` có thể bỏ.
- Nếu chọn JavaFX thì giữ `fxml/`.
- `reports/` chỉ là nơi dự kiến chứa JasperReports template sau này.
- Chưa tạo file `.jrxml` nếu chưa được yêu cầu.
- Chưa tạo model/entity cụ thể.
- Chưa tạo DAO cụ thể.
- Chưa tạo service cụ thể.

---

## 5. Các quyết định cần người dùng xác nhận

Trước khi hoàn thiện setup, AI Agent phải tổng hợp các quyết định sau.

### 5.1. UI framework

Chọn một:

- Java Swing.
- JavaFX.

Nếu chọn JavaFX, cần xem xét thêm:

- Có dùng FXML hay code UI trực tiếp bằng Java.
- Có dùng Scene Builder hay không.

Không tự quyết định.

---

### 5.2. Database

Chọn một:

- MySQL.
- Microsoft SQL Server.

Tùy lựa chọn sẽ dùng JDBC driver tương ứng.

Không cài cả hai driver nếu chưa có yêu cầu.

---

### 5.3. Java version

Cần xác nhận version Java.

Các lựa chọn nên cân nhắc:

- Java 17 LTS.
- Java 21 LTS.
- Version do giảng viên hoặc môi trường trường yêu cầu.

AI Agent không được tự nâng version nếu có giới hạn từ môi trường học tập.

---

### 5.4. Build tool

Chọn một:

- Maven.
- Gradle.

Nếu người dùng chưa quyết định, chỉ ghi chú và không tạo cấu hình dependency cuối cùng.

---

### 5.5. IDE

Có thể sử dụng:

- IntelliJ IDEA.
- Eclipse.
- VS Code.
- NetBeans.

IDE không được làm ảnh hưởng cấu trúc project nếu chưa có yêu cầu cụ thể.

---

### 5.6. Database runtime

Cần quyết định:

- Cài MySQL / SQL Server trực tiếp trên máy.
- Chạy database bằng Docker.

Nếu dùng Docker, chỉ setup container sau khi người dùng xác nhận.

---

### 5.7. Database configuration

Cần quyết định cách lưu:

- JDBC URL.
- Username.
- Password.
- Database name.

Không hard-code password thật vào Git repository.

Có thể cân nhắc:

- `.properties`.
- `.env` + thư viện hỗ trợ.
- Environment variables.

Không thêm thư viện `.env` nếu chưa được chốt.

---

## 6. Dependency dự kiến

Chỉ thêm dependency sau khi lựa chọn tương ứng được xác nhận.

### 6.1. JDBC Driver

Nếu MySQL:

```text
MySQL Connector/J
```

Nếu SQL Server:

```text
Microsoft JDBC Driver for SQL Server
```

Không cài cả hai nếu không cần.

---

### 6.2. JavaFX

Chỉ cần nếu chọn JavaFX.

Có thể cần:

```text
javafx-controls
javafx-fxml
```

Không thêm nếu chọn Swing.

---

### 6.3. Apache POI

Sử dụng cho chức năng xuất Excel sau này.

Dependency chính dự kiến:

```text
poi-ooxml
```

Ở giai đoạn setup chỉ cần chuẩn bị dependency nếu người dùng muốn cài ngay.

Chưa viết code export Excel.

---

### 6.4. JasperReports

Sử dụng cho:

- In hóa đơn.
- Preview hóa đơn.
- Xuất hóa đơn PDF nếu cần.

Ở giai đoạn setup:

- Chỉ thêm dependency nếu được xác nhận.
- Chưa tạo report template.
- Chưa tích hợp datasource.
- Chưa viết code compile/fill/export report.

---

## 7. File cấu hình database dự kiến

Không hard-code cấu hình trong DAO.

Có thể chuẩn bị:

```text
src/main/resources/config/database.properties
```

Ví dụ cấu trúc:

```properties
db.url=
db.username=
db.password=
```

Chỉ là placeholder.

Không commit credential thật.

Nếu người dùng quyết định dùng environment variables, thay đổi cách cấu hình sau.

---

## 8. Database connection skeleton

Trong giai đoạn setup chỉ có thể tạo skeleton như:

```text
config/
└── DatabaseConnection.java
```

Nhiệm vụ tương lai của class này:

- Đọc cấu hình database.
- Tạo JDBC `Connection`.
- Trả connection cho DAO.

Không viết query nghiệp vụ.

Không tạo connection pool trừ khi được yêu cầu.

---

## 9. Main application skeleton

Chỉ tạo entry point tối thiểu.

Ví dụ conceptual:

```text
Main.java
```

Nhiệm vụ:

- Khởi chạy Java application.
- Nếu dùng JavaFX: launch JavaFX Application.
- Nếu dùng Swing: tạo main window trên EDT.

Không dựng màn hình POS hoàn chỉnh.

Có thể tạo một cửa sổ placeholder để xác nhận project chạy thành công nếu người dùng cho phép.

---

## 10. `.gitignore`

Setup phải có `.gitignore`.

Tối thiểu cần bỏ qua:

```text
target/
build/
out/
.idea/
*.iml
.classpath
.project
.settings/
.vscode/
*.log
.env
```

Có thể điều chỉnh theo IDE được chọn.

Không ignore source code hoặc report template.

---

## 11. README ban đầu

README chỉ cần chứa:

- Tên project.
- Mục tiêu ngắn gọn.
- Stack đã được xác nhận.
- Yêu cầu môi trường.
- Cách chạy project.
- Các quyết định chưa chốt.
- Trạng thái hiện tại: setup only.

Không mô tả chức năng chưa triển khai như thể đã hoàn thành.

---

## 12. Setup notes

AI Agent phải duy trì:

```text
docs/SETUP_NOTES.md
```

Mỗi khi cài hoặc thêm một thành phần, ghi:

```text
- Thành phần:
- Mục đích:
- Version:
- Cách cài:
- File cấu hình liên quan:
- Trạng thái:
```

Ví dụ:

```text
- Thành phần: JDK
- Mục đích: Biên dịch và chạy ứng dụng Java
- Version: Chưa chốt
- Cách cài: Chưa thực hiện
- File cấu hình liên quan: N/A
- Trạng thái: Pending decision
```

---

## 13. Checklist setup

AI Agent sử dụng checklist sau. Cập nhật ngày 2026-09-07 theo [docs/SETUP_NOTES.md](docs/SETUP_NOTES.md).

```text
[x] Xác nhận Java version (21)
[x] Xác nhận Swing hoặc JavaFX (Swing)
[x] Xác nhận MySQL hoặc SQL Server (SQL Server)
[x] Xác nhận Maven hoặc Gradle (Maven)
[x] Xác nhận IDE (Zed)
[x] Xác nhận database chạy local hay Docker (Docker SQL Server local)
[x] Khởi tạo project và cấu hình Maven
[x] Tạo package structure
[x] Tạo resources structure
[x] Tạo .gitignore
[x] Tạo README
[x] Tạo SETUP_NOTES.md
[x] Thêm JDBC driver (Microsoft JDBC 13.4.0.jre11)
[x] Thêm UI dependency nếu cần (Swing có sẵn trong JDK)
[ ] Thêm Apache POI nếu muốn setup ngay — Deferred
[ ] Thêm JasperReports nếu muốn setup ngay — Deferred
[x] Tạo database config placeholder (example; chưa đọc cấu hình)
[x] Tạo DatabaseConnection skeleton
[x] Tạo Main application skeleton (cửa sổ Swing trên EDT)
[x] Build project thành công
[x] Chạy application skeleton thành công
[x] Tạo và validate Docker Compose
[ ] Khởi chạy SQL Server Docker healthy
[ ] Kiểm tra JDBC tới SQL Server local
```

Java/Maven skeleton đã build và chạy thành công. SQL Server Docker đang tải image; chưa xác minh container healthy hoặc JDBC. Không còn quyết định stack chặn build. POI/JasperReports để sau; chưa tạo schema/nghiệp vụ. Xem [hướng dẫn chạy](docs/INSTALLATION.md).

---

## 14. Definition of Done cho giai đoạn setup

Giai đoạn setup chỉ được xem là hoàn thành khi:

1. Project compile/build thành công.
2. Application skeleton chạy được.
3. Structure project rõ ràng.
4. Dependency đã cài đúng theo các lựa chọn được người dùng xác nhận.
5. Không có nghiệp vụ POS được triển khai ngoài phạm vi setup.
6. Không chứa database password thật trong repository.
7. `README.md` phản ánh đúng trạng thái project.
8. `docs/SETUP_NOTES.md` ghi lại toàn bộ công cụ/dependency đã thêm.
9. Các quyết định chưa được chốt vẫn được đánh dấu rõ là `Pending`.
10. Không tự ý thêm framework hoặc thư viện ngoài scope.

---

## 15. Hành vi bắt buộc của AI Agent

Khi nhận tài liệu này, AI Agent phải:

- Chỉ làm việc trong phạm vi setup.
- Kiểm tra project hiện tại trước khi sửa.
- Không ghi đè cấu hình đang tồn tại nếu chưa cần thiết.
- Không tự quyết định các lựa chọn còn `Pending`.
- Ghi lại mọi dependency/công cụ đã thêm.
- Giữ thay đổi nhỏ và dễ review.
- Sau khi setup, chạy build để kiểm tra.
- Nếu có lỗi môi trường, báo rõ nguyên nhân và phần còn thiếu.
- Không chuyển sang viết nghiệp vụ cho đến khi người dùng yêu cầu.

Nếu cần cài thêm phần mềm hoặc dependency ở hệ điều hành, AI Agent phải ghi rõ:

```text
Cần cài:
Mục đích:
Lý do:
Lệnh cài đề xuất:
Trạng thái: Chờ người dùng quyết định
```

Không tự cài phần mềm hệ thống nếu chưa được cho phép.

---

## 16. Những việc KHÔNG làm ở giai đoạn này

Không thực hiện:

- Thiết kế database schema chi tiết.
- Tạo bảng database.
- Seed data.
- Product CRUD.
- Customer CRUD.
- Invoice CRUD.
- Xử lý tồn kho.
- Transaction bán hàng.
- Tính VAT.
- Tính discount.
- Dashboard.
- Revenue report.
- Best-selling products.
- Excel export implementation.
- JasperReports invoice implementation.
- Authentication.
- Authorization.
- Unit test nghiệp vụ.
- UI hoàn chỉnh.
- Packaging installer.

Các phần trên thuộc giai đoạn sau.

---

## 17. Trạng thái ban đầu

```text
PROJECT PHASE: SETUP
BUSINESS LOGIC: NOT STARTED
DATABASE SCHEMA: NOT STARTED
UI IMPLEMENTATION: NOT STARTED
REPORTING: NOT STARTED
```

Mục tiêu duy nhất hiện tại:

> Tạo một nền tảng Java Desktop sạch, build được, chạy được và sẵn sàng để bắt đầu phát triển sau khi các lựa chọn kỹ thuật được người dùng xác nhận.
