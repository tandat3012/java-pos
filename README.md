# POS Java Desktop

Project Java Desktop đang ở giai đoạn **giao diện mẫu**, theo [yêu cầu bài toán](docs/instruction.md). Đã có sáu màn hình Swing dùng dữ liệu giả trong bộ nhớ: Tổng quan, Bán hàng, Sản phẩm, Khách hàng, Hóa đơn và Báo cáo. Chưa kết nối database thật; dữ liệu đặt lại khi mở ứng dụng.

## Chạy ứng dụng

Yêu cầu JDK 21 và Maven 3.9.x. Tại thư mục project:

```sh
mvn clean verify
mvn compile exec:java
```

Ứng dụng mở cửa sổ An Nhiên POS; đóng cửa sổ để thoát. Không cần chạy database để xem giao diện. Cần phiên desktop có màn hình; kích thước tối thiểu hiện tại là 1180 × 800.

Đã kiểm tra build, render sáu màn hình và các thao tác mẫu. Xem [tiến độ, cách thử và giới hạn](docs/UI_PROTOTYPE.md). `mvn verify` kiểm tra build; smoke runner được chạy riêng, không tự chạy bởi Surefire.

## Database local

SQL Server 2022 Developer chạy bằng Docker Compose, cùng loại database dự kiến dùng khi demo Windows. Kết nối local tại `localhost:5436`; container dùng cổng `1433`.

```sh
docker compose --env-file .local/sqlserver.env up -d --wait
docker compose --env-file .local/sqlserver.env ps
```

File `.local/sqlserver.env` chứa mật khẩu local, được ignore và không phân phối cùng project. Nếu chưa có file, làm theo [hướng dẫn cài và chạy](docs/INSTALLATION.md). Dữ liệu nằm trong named volume. Chưa tạo database ứng dụng hoặc schema.

## Cấu trúc và cấu hình

- `src/main/java/pos/Main.java`: khởi chạy giao diện trên EDT.
- `src/main/java/pos/view/`: khung điều hướng, sáu màn hình và hộp thoại xem hóa đơn.
- `src/main/java/pos/demo/DemoStore.java`: dữ liệu giả và thao tác trong phiên; không dùng JDBC.
- `src/test/java/pos/PrototypeSmoke.java`: kiểm tra dữ liệu mẫu và render giao diện.
- `src/main/java/pos/config/DatabaseConnection.java`: skeleton, chưa mở kết nối.
- Các package `model`, `dao`, `service`, `controller` dành cho triển khai sau.
- `src/main/resources/config/database.properties.example`: mẫu cấu hình rỗng. Bản local `database.properties` được ignore và loại khỏi Maven resources/JAR; skeleton chưa đọc file này.
- `src/main/java/pos/util/ExcelExporter.java`: code xuất báo cáo Excel hiện có với Apache POI 5.3.0; chưa kiểm chứng ghi file trong lần chạy thử UI này.
- `src/main/resources/reports/`: chưa có template; in/PDF/Excel hóa đơn vẫn là các nút chưa triển khai.

Dùng Zed trên Linux; source/build dùng được cho Windows với JDK 21 và Maven. Chưa tạo installer hoặc kiểm chứng trên Windows.

Xem [setup notes và kết quả kiểm tra](docs/SETUP_NOTES.md), [lựa chọn người dùng](docs/SETUP_DECISIONS.md), [checklist](POS_JAVA_SETUP_AGENT_INSTRUCTIONS.md#13-checklist-setup).
