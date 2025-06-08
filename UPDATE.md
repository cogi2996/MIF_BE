# Flow Xử lý Report và Block Post

## 1. Flow chính

1. Người dùng report bài viết
2. Admin xem và xử lý report
   2.1 khi bấm vào một report thì
   2.1.1 hiện số lượng người đã report bài viết
   2.1.2 hiện phân tích AI
3. Admin block post nếu vi phạm
   - Hệ thống tự động cập nhật tất cả report của bài viết thành đã xử lý
4. Hệ thống gửi thông báo cho chủ bài viết

## 2. Các API liên quan

### 2.1. API cho người dùng

```typescript
// Report bài viết
POST /api/group-posts/{postId}/report
{
  "reason": string,
}
```

### 2.2. API cho Admin

// Lấy danh sách report trong group
GET /api/group-posts/{groupId}/reports
Query:

- page: số trang
- size: số lượng mỗi trang
- status: "resolved" | "unresolved" (đã xử lý/chưa xử lý)
- sortBy: sắp xếp theo trường
- sortDirection: "asc" | "desc"

// Xem chi tiết report
GET /api/group-posts/reports/{reportId}/analyze

// Xem số lượng report của bài viết
GET /api/group-posts/reports/{postId}/count

// Block bài viết
POST /api/group-posts/{postId}/block

````

## 3. Thông báo khi post bị block

```typescript
{
  type: "POST_BLOCKED",
  receiverId: string,  // ID chủ bài viết
  senderId: string,   // ID admin
  groupId: string,
  groupPostId: string,
  message: "Bài đăng của bạn đã bị chặn bởi quản trị viên vì vi phạm tiêu chuẩn cộng đồng!"
}
````
