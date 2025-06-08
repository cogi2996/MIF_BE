Chức năng phim được nhắc đến nhiều nhất
FLOW:

1. tạo entity ReferenceMovie
   // mục đích mỗi lần có ai dùng api createPost thì trong hàm đó check xem post có tham chiếu đến phim nào không
   // với các post có tham chiếu đến phim thì content sẽ chứ kí tự như @[tên phim](id phim)
   // ví dụ : @[Inception](683f2e74f259647e8295a6b1)
   - entity này dùng để thống kê cho biết được phim nào, ở group nào , được nhắc mấy lần
