package pos.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Session-only fixtures and interactions for reviewing the UI, never a database repository. */
public final class DemoStore {
    public record Product(String id, String name, String category, String unit, long price, int stock, int minimum) {
        public boolean lowStock() { return stock < minimum; }
        public Product withStock(int value) { return new Product(id, name, category, unit, price, value, minimum); }
    }
    public record Customer(String id, String name, String phone) {
        @Override public String toString() { return name + (phone.isBlank() ? "" : " · " + phone); }
    }
    public record Line(String productId, String name, String unit, long price, int quantity) {
        public long total() { return price * quantity; }
    }
    public record Invoice(String id, LocalDateTime date, Customer customer, List<Line> lines,
                          int discountPercent, int vatPercent) {
        public Invoice { lines = List.copyOf(lines); }
        public long subtotal() { return lines.stream().mapToLong(Line::total).sum(); }
        public long discount() { return percent(subtotal(), discountPercent); }
        public long vat() { return percent(subtotal() - discount(), vatPercent); }
        public long total() { return subtotal() - discount() + vat(); }
    }

    public static final Customer GUEST = new Customer("", "Khách lẻ", "");
    private final List<Product> products = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Invoice> invoices = new ArrayList<>();
    private int invoiceSequence = 1013;

    public DemoStore() {
        products.addAll(List.of(
            new Product("SP001", "Cà phê sữa đá", "Đồ uống", "Ly", 29000, 46, 10),
            new Product("SP002", "Trà đào cam sả", "Đồ uống", "Ly", 35000, 32, 10),
            new Product("SP003", "Nước suối 500ml", "Đồ uống", "Chai", 10000, 8, 15),
            new Product("SP004", "Bánh mì thịt", "Đồ ăn", "Phần", 25000, 24, 5),
            new Product("SP005", "Bánh croissant", "Đồ ăn", "Cái", 32000, 4, 8),
            new Product("SP006", "Sữa tươi ít đường", "Sữa & thực phẩm", "Hộp", 8500, 64, 12),
            new Product("SP007", "Mì ly hải sản", "Sữa & thực phẩm", "Ly", 16000, 28, 10),
            new Product("SP008", "Khăn giấy bỏ túi", "Tiện ích", "Gói", 5000, 0, 10),
            new Product("SP009", "Nước cam ép", "Đồ uống", "Ly", 38000, 19, 5),
            new Product("SP010", "Bánh quy bơ", "Đồ ăn", "Hộp", 45000, 18, 5),
            new Product("SP011", "Trà xanh đóng chai", "Đồ uống", "Chai", 12000, 40, 10),
            new Product("SP012", "Kẹo bạc hà", "Tiện ích", "Hộp", 15000, 21, 5)));
        customers.addAll(List.of(
            new Customer("KH001", "Nguyễn Minh Anh", "0901234567"),
            new Customer("KH002", "Trần Hoàng Nam", "0912345678"),
            new Customer("KH003", "Lê Thu Hà", "0987654321"),
            new Customer("KH004", "Phạm Quốc Bảo", "0934567890"),
            new Customer("KH005", "Đặng Ngọc Linh", "0967890123")));
        for (int i = 0; i < 12; i++) {
            Product a = products.get(i % 7), b = products.get((i + 3) % 7);
            invoices.add(new Invoice("HD" + (1001 + i), LocalDate.now().minusDays(i % 7).atTime(9 + i % 8, i * 5 % 60),
                i % 3 == 0 ? GUEST : customers.get(i % customers.size()),
                List.of(line(a, 2 + i % 4), line(b, 1 + i % 3)), i % 4 == 0 ? 5 : 0, 0));
        }
    }

    public List<Product> products() { return List.copyOf(products); }
    public List<Customer> customers() { return List.copyOf(customers); }
    public List<Invoice> invoices() {
        return invoices.stream().sorted((a, b) -> b.date().compareTo(a.date())).toList();
    }
    public Product product(String id) {
        return products.stream().filter(p -> p.id().equals(id)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không còn trong danh sách. Vui lòng bỏ khỏi giỏ."));
    }
    public void saveProduct(Product value, String originalId) {
        require(!value.id().isBlank() && !value.name().isBlank() && !value.category().isBlank() && !value.unit().isBlank(),
            "Vui lòng nhập đủ mã, tên, loại và đơn vị tính.");
        require(value.price() > 0 && value.price() <= 1_000_000_000L, "Đơn giá phải từ 1 đến 1.000.000.000 đ.");
        require(value.stock() >= 0 && value.minimum() >= 0, "Số lượng tồn và ngưỡng cảnh báo không được âm.");
        require(products.stream().noneMatch(p -> p.id().equalsIgnoreCase(value.id()) && !p.id().equals(originalId)),
            "Mã sản phẩm đã tồn tại.");
        if (originalId == null) products.add(value);
        else products.set(products.indexOf(product(originalId)), value);
    }
    public void removeProduct(String id) { products.removeIf(p -> p.id().equals(id)); }
    public void saveCustomer(Customer value, String originalId) {
        require(!value.id().isBlank() && !value.name().isBlank(), "Vui lòng nhập mã và họ tên khách hàng.");
        require(value.phone().matches("[0-9]{9,11}"), "Số điện thoại cần có từ 9 đến 11 chữ số.");
        require(customers.stream().noneMatch(c -> c.id().equalsIgnoreCase(value.id()) && !c.id().equals(originalId)),
            "Mã khách hàng đã tồn tại.");
        if (originalId == null) customers.add(value);
        else {
            for (int i = 0; i < customers.size(); i++) if (customers.get(i).id().equals(originalId)) {
                customers.set(i, value); return;
            }
            throw new IllegalArgumentException("Khách hàng không còn tồn tại.");
        }
    }
    public Invoice preview(Map<String, Integer> cart, Customer customer, int discount, int vat) {
        require(discount >= 0 && discount <= 100 && vat >= 0 && vat <= 100, "Tỷ lệ phải trong khoảng 0–100%.");
        List<Line> lines = new ArrayList<>();
        for (var item : cart.entrySet()) {
            Product product = product(item.getKey());
            require(item.getValue() > 0, "Số lượng phải lớn hơn 0.");
            require(item.getValue() <= product.stock(), "Sản phẩm “" + product.name() + "” không đủ tồn kho mẫu.");
            lines.add(line(product, item.getValue()));
        }
        return new Invoice("HD" + invoiceSequence, LocalDateTime.now(), customer == null ? GUEST : customer, lines, discount, vat);
    }
    public Invoice checkout(Map<String, Integer> cart, Customer customer, int discount, int vat) {
        require(!cart.isEmpty(), "Hãy thêm ít nhất một sản phẩm vào hóa đơn.");
        Invoice invoice = preview(cart, customer, discount, vat);
        for (Line line : invoice.lines()) {
            Product p = product(line.productId());
            products.set(products.indexOf(p), p.withStock(p.stock() - line.quantity()));
        }
        invoices.add(invoice);
        invoiceSequence++;
        return invoice;
    }
    public static long percent(long amount, int rate) {
        return BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(rate))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP).longValueExact();
    }
    public static Map<String, Long> bestSellers(List<Invoice> invoices) {
        Map<String, Long> result = new LinkedHashMap<>();
        invoices.forEach(i -> i.lines().forEach(l -> result.merge(l.name(), (long) l.quantity(), Long::sum)));
        return result;
    }
    private static Line line(Product p, int quantity) { return new Line(p.id(), p.name(), p.unit(), p.price(), quantity); }
    private static void require(boolean valid, String message) { if (!valid) throw new IllegalArgumentException(message); }
}
