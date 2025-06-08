# Kế hoạch phát triển Module ActivityAnalytics

## 1. Mục tiêu

Phát triển hệ thống phân tích hoạt động thành viên và trao huy hiệu dựa trên mức độ tích cực trong nhóm.

## 2. Các chức năng chính

### 2.1. Phân tích hoạt động thành viên

- Theo dõi các hoạt động của thành viên:
  - Tham gia nhóm (GROUP_JOINED)
  - Đăng bài viết (GROUP_POST_CREATED)
  - Tương tác với bài viết (GROUP_POST_LIKED)
  - Bình luận (GROUP_COMMENT_CREATED)
  - Tương tác với bình luận (GROUP_COMMENT_LIKED)
  - Tham gia sự kiện (GROUP_EVENT_JOINED)
  - Nhận huy hiệu (BADGE_EARNED)

### 2.2. Hệ thống tính điểm

- Công thức tính điểm hoạt động:
  - Tham gia nhóm: 20 điểm
  - Đăng bài viết: 10 điểm
  - Like bài viết: 1 điểm
  - Bình luận: 5 điểm
  - Like bình luận: 1 điểm
  - Tham gia sự kiện: 15 điểm
  - Nhận huy hiệu: 30 điểm

### 2.3. Hệ thống huy hiệu

- Các loại huy hiệu:
  - Huy hiệu tích cực (dựa trên tần suất hoạt động)
  - Huy hiệu chất lượng (dựa trên đánh giá của thành viên)
  - Huy hiệu đặc biệt (cho các đóng góp nổi bật)
- Cấp độ huy hiệu:
  - Đồng (Bronze)
  - Bạc (Silver)
  - Vàng (Gold)
  - Bạch kim (Platinum)

## 3. Cấu trúc dữ liệu

### 3.1. Entity

- MemberActivity

  - userId
  - groupId
  - activityType
  - points
  - timestamp
  - metadata

- Badge

  - badgeId
  - name
  - description
  - level
  - criteria
  - imageUrl

- MemberBadge
  - userId
  - badgeId
  - earnedDate
  - expirationDate (nếu có)

### 3.2. DTO

- ActivityDTO
- BadgeDTO
- MemberActivitySummaryDTO
- BadgeEarningCriteriaDTO

## 4. Background Tasks và Scheduled Jobs

### 4.1. Activity Tracking Tasks

- Consumer cho queue `activity.tracking`:
  - Xử lý các sự kiện hoạt động từ các module khác
  - Lưu trữ thông tin hoạt động vào MongoDB
  - Trigger tính toán điểm

### 4.2. Points Calculation Tasks

- Consumer cho queue `points.calculation`:
  - Tính toán điểm số theo công thức
  - Cập nhật điểm số vào database
  - Trigger đánh giá huy hiệu

### 4.3. Badge Evaluation Tasks

- Consumer cho queue `badge.evaluation`:
  - Kiểm tra điều kiện trao huy hiệu
  - Tự động trao huy hiệu khi đủ điều kiện
  - Gửi thông báo qua queue `notification.badge`

### 4.4. Scheduled Jobs

- Tính toán điểm tổng hợp hàng ngày
- Cập nhật bảng xếp hạng hàng tuần
- Dọn dẹp dữ liệu hoạt động cũ
- Backup dữ liệu analytics

## 5. Công nghệ sử dụng

- Spring Boot
- MongoDB
- Redis (cho caching)
- Quartz (cho scheduled tasks)
- Spring WebSocket (cho real-time updates)
- RabbitMQ (cho các tác vụ messaging bất đồng bộ)

### 5.1. RabbitMQ Integration

- Sử dụng cho các tác vụ:
  - Tracking hoạt động người dùng (asynchronous)
  - Tính toán điểm số (background job)
  - Cập nhật huy hiệu (event-driven)
  - Gửi thông báo khi đạt huy hiệu mới
- Các queue chính:
  - `activity.tracking` - Nhận các sự kiện hoạt động
  - `points.calculation` - Xử lý tính toán điểm
  - `badge.evaluation` - Đánh giá điều kiện trao huy hiệu
  - `notification.badge` - Gửi thông báo huy hiệu
- Exchange và Routing:
  - Direct Exchange: `activity.exchange`
  - Topic Exchange: `badge.exchange`
  - Dead Letter Exchange: `dlx.exchange`

## 6. Lịch trình phát triển

### Phase 1: Cơ bản và RabbitMQ Setup (1.5 tuần)

- Thiết lập cấu trúc module
- Cấu hình RabbitMQ
  - Setup exchanges và queues
  - Implement basic message producers
  - Setup dead letter handling
- Tạo entity và repository cơ bản

### Phase 2: Activity Tracking System (1.5 tuần)

- Implement activity tracking consumers
- Setup message retry mechanism
- Implement data storage strategy
- Setup monitoring cho activity tracking

### Phase 3: Points Calculation System (1.5 tuần)

- Implement points calculation consumers
- Develop scoring algorithms
- Setup batch processing for points
- Implement scheduled jobs cho điểm số

### Phase 4: Badge System (1.5 tuần)

- Implement badge evaluation consumers
- Develop badge assignment logic
- Setup notification system
- Implement scheduled jobs cho huy hiệu

### Phase 5: Optimization and Monitoring (1 tuần)

- Implement caching strategy
- Setup monitoring cho RabbitMQ và scheduled jobs
- Performance optimization
- Testing và bug fixing

## 7. Các vấn đề cần lưu ý

- Xử lý concurrent activity tracking
- Tối ưu hiệu suất khi tính toán điểm
- Bảo mật thông tin hoạt động
- Khả năng mở rộng trong tương lai
- Xử lý message loss và retry mechanism
- Monitoring RabbitMQ queues và performance
- Message ordering và consistency
- Dead letter handling và message recovery
- Queue monitoring và alerting
- Load balancing cho consumers
- Monitoring và alerting cho scheduled jobs
- Xử lý lỗi và recovery cho background tasks
