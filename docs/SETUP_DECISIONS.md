# Thông tin cần cung cấp để tiếp tục setup

Bạn điền vào các dòng **Trả lời** bên dưới rồi báo agent đọc lại file này. Có thể ghi `Pending` nếu chưa quyết định. Các ví dụ chỉ minh họa lựa chọn, không phải cấu hình đã được chốt.

Không ghi mật khẩu, token hoặc credentials thật vào file này.

## 1. Yêu cầu của giảng viên / môi trường học tập

Có bắt buộc Java version, UI, database, IDE hoặc công cụ nào không? Nếu không, ghi `Không có`.

**Trả lời:** Không có

## 2. Java version

Chọn Java 17, Java 21 hoặc ghi rõ phiên bản khác theo yêu cầu.

**Trả lời:** java 21 

## 3. Giao diện desktop

Chọn `Swing` hoặc `JavaFX`.

**Trả lời:** Swing

Nếu chọn JavaFX, điền thêm; nếu chọn Swing, ghi `Không áp dụng`:

- Cách dựng UI (`FXML` hoặc `Java trực tiếp`): Không áp dụng (Swing)
- Dùng Scene Builder (`Có` hoặc `Không`): Không áp dụng (Swing)

Cho phép tạo một cửa sổ placeholder tối thiểu để kiểm tra ứng dụng chạy được (`Có` hoặc `Không`; chưa làm UI chức năng): Có — xác nhận trong hội thoại

## 4. Database

Chọn `MySQL` hoặc `Microsoft SQL Server`.

**Trả lời:** SQL Server

Version database: ghi phiên bản bắt buộc, phiên bản đang có hoặc `Giao agent chọn phiên bản tương thích`.

**Trả lời:** Pending

## 5. Build tool

Chọn `Maven` hoặc `Gradle`.

**Trả lời:** Maven — xác nhận trong hội thoại

## 6. IDE

Ghi IDE dự định dùng: IntelliJ IDEA, Eclipse, VS Code, NetBeans hoặc IDE khác.

**Trả lời:** hiện đang dùng zed

## 7. Môi trường chạy database

Chọn `Local` hoặc `Docker`. Nếu database đã có sẵn, mô tả môi trường hiện tại, không cung cấp credentials thật.

**Trả lời:** Hiện tại dùng docker, xong setup sẽ dùng sql server

Nếu chọn Docker, cho phép agent tạo cấu hình và khởi chạy container database phục vụ phát triển (`Có` hoặc `Không`): có dùng 5436:5432

## 8. Quản lý cấu hình kết nối database

Chọn một cách:

- File `.properties` local, không commit credentials.
- Biến môi trường.
- File `.env` local; cần xác nhận thư viện hỗ trợ nếu sử dụng. 

**Trả lời:** .properties

Ở bước setup chỉ cần placeholder cho JDBC URL, database name, username và password. Không cần cung cấp thông tin đăng nhập thật.

## 9. Apache POI và JasperReports

Điền `Thêm ngay` hoặc `Để sau` cho từng thư viện. Chỉ thêm dependency; chưa viết xuất Excel, hóa đơn hoặc report template.

- Apache POI: Để sau
- JasperReports: Để 

## 10. Phiên bản công cụ / thư viện

Bạn muốn tự chỉ định version hay cho phép agent tra cứu và chọn version tương thích với stack đã chốt?

**Trả lời:** cho phép tra cứu theo version đã chọn

Nếu tự chỉ định, điền các mục áp dụng; có thể ghi `Không áp dụng` hoặc `Để sau`:

- Maven / Gradle: Pending
- JDBC driver tương ứng database đã chọn: Pending
- JavaFX (nếu chọn): Pending
- Apache POI (nếu thêm ngay): Pending
- JasperReports (nếu thêm ngay): Pending
- Thư viện đọc `.env` và version (nếu chọn): Pending

## 11. Cài công cụ còn thiếu

Lần kiểm tra setup trước chưa tìm thấy `java`, `javac`, `mvn`, `gradle` trên PATH. Có Docker CLI nhưng chưa kiểm tra daemon.

Chọn `Tôi tự cài` hoặc `Cho phép agent cài các công cụ đã chốt còn thiếu`. Nếu chỉ cho phép một phần, liệt kê rõ công cụ và giới hạn.

**Trả lời:** Pending

Nếu đã cài JDK/build tool ở vị trí khác hoặc có yêu cầu cách cài, ghi đường dẫn và hướng dẫn tại đây:

**Trả lời:**  hướng dẫn cài 

## 12. Ghi chú thêm (không bắt buộc)

Ví dụ: hệ điều hành đích, quy ước tên package, giới hạn tải dependency, yêu cầu của nhóm.

**Trả lời:** Hiện tại dùng linux, đích đến để dùng window demo báo cáo,

---

Sau khi bạn điền thông tin, agent sẽ cập nhật README, setup notes và checklist; hoàn thiện cấu hình theo các lựa chọn đã xác nhận; chạy build và kiểm tra application skeleton. Mục còn `Pending` tiếp tục để mở, không tự chọn. Phạm vi vẫn chỉ là setup, chưa triển khai nghiệp vụ hoặc database schema.


## Xác nhận bổ sung trong hội thoại (2026-09-07)

- Người dùng chốt Maven, dùng Docker host database local để demo với SQL Server, và cho phép tiếp tục setup/cửa sổ mẫu.
- Agent triển khai SQL Server trong Docker ngay từ môi trường phát triển, dùng host port 5436 đã đề nghị, ánh xạ sang container port 1433 của SQL Server. Dòng 5436:5432 phía trên được giữ làm lịch sử, không phải cấu hình cuối.
- Chọn SQL Server 2022 Developer (`2022-latest`) và JDBC 13.4.0.jre11 theo quyền tra cứu version tương thích ở mục 10. Tag image có thể nhận bản vá mới; digest thực tế được ghi ở setup notes sau khi pull.
- JDK 21.0.12 và Maven 3.9.16 hiện đã có trên máy; agent không cài phần mềm hệ thống.
- JavaFX, Scene Builder và thư viện .env Java không áp dụng. File .local/sqlserver.env chỉ dùng cho Docker Compose; cấu hình Java vẫn là .properties.
- Apache POI để sau. JasperReports ghi “Để”, tiếp tục để sau và chưa thêm dependency.
