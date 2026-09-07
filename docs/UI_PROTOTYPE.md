# Tiến độ giao diện mẫu

Cập nhật theo code và lần chạy thử ngày 2026-09-07. Phạm vi được người dùng mở rộng từ setup sang dựng màn hình mẫu theo `docs/instruction.md`, trước khi dùng dữ liệu thật từ DB.

## Các màn hình hiện có

| Màn hình | Nội dung và tương tác mẫu |
| --- | --- |
| Tổng quan | Doanh thu hôm nay, số hóa đơn, cảnh báo tồn, biểu đồ 7 ngày, hóa đơn gần đây |
| Bán hàng | Tìm/lọc sản phẩm, chọn khách hoặc khách lẻ, số lượng, giỏ hàng, chiết khấu/VAT, xác nhận thanh toán và xem trước hóa đơn |
| Sản phẩm | Mã, tên, loại, đơn vị, giá, tồn kho; tìm kiếm, lọc, thêm/sửa/xóa trong phiên và cảnh báo dưới ngưỡng |
| Khách hàng | Mã, họ tên, SĐT; tìm kiếm và thêm/sửa thông tin trong phiên |
| Hóa đơn | Danh sách mẫu, tìm mã, lọc ngày, xem chi tiết sản phẩm và tổng tiền |
| Báo cáo | Kỳ ngày/tháng, doanh thu, số hóa đơn, giá trị trung bình, sản phẩm bán chạy và bảng chi tiết |

Nguồn dữ liệu là `pos.demo.DemoStore`: 12 sản phẩm, 5 khách hàng và 12 hóa đơn giả ban đầu. Thao tác thanh toán cập nhật tồn kho/hóa đơn mẫu trong bộ nhớ. Mở ứng dụng mới sẽ tạo lại dữ liệu ban đầu. Không có kết nối SQL Server hoặc schema ứng dụng trong luồng UI này.

## Chạy thử

```sh
mvn compile exec:java
```

Đây là goal phù hợp với `mainClass=pos.Main` trong POM hiện tại. Lệnh `exec:exec` cũ không còn có executable cấu hình sẵn.

Gợi ý thử: vào Bán hàng → chọn một sản phẩm → Thêm vào hóa đơn → chọn dòng và tăng/giảm số lượng → chọn khách → nhập chiết khấu/VAT → Thanh toán mẫu. Sau khi xác nhận, kiểm tra Hóa đơn, Tổng quan và Báo cáo trong cùng phiên.

## Kết quả đã kiểm tra

- [x] Maven verify thành công với dependency hiện tại, gồm JDBC và Apache POI 5.3.0.
- [x] Sáu màn hình render tại 1380 × 900 và 1180 × 800; ảnh nằm ở `target/ui-preview/`.
- [x] Điều hướng đánh dấu đúng màn hình đang chọn.
- [x] Tìm sản phẩm không dấu, trạng thái không có kết quả, tìm khách theo SĐT.
- [x] Thêm dòng giỏ hàng, tăng số lượng, bỏ dòng; chặn thanh toán khi giỏ trống.
- [x] Dữ liệu mẫu: tính chiết khấu/VAT, cập nhật tồn/hóa đơn, từ chối số lượng không hợp lệ mà không thay đổi một phần dữ liệu.
- [x] Hóa đơn giữ tên/giá tại thời điểm tạo dù sản phẩm sau đó được sửa.
- [x] Mở phiên mới đặt lại dữ liệu giả.
- [ ] Kiểm thử toàn bộ hộp thoại và thao tác xuất Excel ghi file.
- [ ] Kiểm chứng trên Windows.
- [ ] Tích hợp dữ liệu thật từ SQL Server.

Smoke runner không cần thêm thư viện test. Chạy kiểm tra dữ liệu riêng:

```sh
mvn test-compile
java -cp target/classes:target/test-classes pos.PrototypeSmoke
```

Trên Windows thay dấu phân cách classpath `:` bằng `;`. Để kiểm tra UI với classpath có đầy đủ dependency trên phiên desktop:

```sh
mvn test-compile exec:exec -Dexec.executable=java -Dexec.classpathScope=test "-Dexec.args=-classpath %classpath pos.PrototypeSmoke --ui target/ui-preview"
```

Lần kiểm tra của agent dùng thêm `-Dmaven.repo.local=/tmp/java-pos-m2`; cache mặc định của người dùng vẫn dùng được. Kết quả runner: `PASS: prototype checks including Swing UI`. Build thành công không đồng nghĩa tất cả các luồng nghiệp vụ đã được kiểm thử.

## Giới hạn hiện tại

Code xuất báo cáo Excel đã có trong project và được giữ nguyên; lần này xác minh compile/render, chưa thử ghi workbook. Các nút in/PDF/Excel trong chi tiết hóa đơn chưa triển khai. Giao diện mẫu chưa phải hệ thống bán hàng dùng dữ liệu thật.

Ở cửa sổ nhỏ nhất, bảng có vùng cuộn và một số tên dài có thể hiển thị rút gọn; có thể kéo rộng cột. Chưa tối ưu cho màn hình thấp hơn 800 px.
