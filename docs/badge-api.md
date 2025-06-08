# Tài liệu API về Huy hiệu (Badge)

## 1. Thông tin chung

Huy hiệu (Badge) là một tính năng để ghi nhận và hiển thị thành tích của người dùng trong các nhóm. Mỗi người dùng có thể có nhiều huy hiệu khác nhau cho từng nhóm mà họ tham gia.

## 2. Các API liên quan đến Badge

### 2.1. API Group

#### 2.1.1. Lấy danh sách lời mời đang chờ

- **Endpoint**: `GET /api/groups/{groupId}/pending-invitations`
- **Mô tả**: Lấy danh sách người dùng đã gửi yêu cầu tham gia nhóm, bao gồm thông tin huy hiệu của họ trong nhóm đó
- **Response**: Tương tự như API lấy danh sách thành viên
- **Response**:
  ```json
  {
    "content": [
      {
        "id": "user_id",
        "displayName": "Tên hiển thị",
        "profilePictureUrl": "url_ảnh",
        "badgeMap": {
          "group_id": "BRONZE" // hoặc SILVER, GOLD, PLATINUM
        }
      }
    ]
  }
  ```

### 2.2. API User

#### 2.2.1. Lấy thông tin người dùng

- **Endpoint**: `GET /api/users/{userId}`
- **Mô tả**: Lấy thông tin chi tiết của người dùng, bao gồm tất cả huy hiệu của họ trong các nhóm
- **Response**:
  ```json
  {
    "id": "user_id",
    "displayName": "Tên hiển thị",
    "profilePictureUrl": "url_ảnh",
    "badgeMap": {
      "group_id_1": "BRONZE",
      "group_id_2": "SILVER",
      "group_id_3": "GOLD"
    }
  }
  ```

## 3. Các cấp độ huy hiệu

- **BRONZE**: Đạt được khi tổng điểm hoạt động >= 100
- **SILVER**: Đạt được khi tổng điểm hoạt động >= 500
- **GOLD**: Đạt được khi tổng điểm hoạt động >= 1000
- **PLATINUM**: Đạt được khi tổng điểm hoạt động >= 2000

## 4. Cách thức hoạt động

1. Hệ thống tự động tính điểm hoạt động của người dùng trong nhóm
2. Khi người dùng đạt đủ điều kiện cho một cấp độ huy hiệu mới:
   - Hệ thống cập nhật `badgeMap` trong thông tin người dùng
   - Gửi thông báo cho người dùng về việc đạt được huy hiệu mới
   - Hiển thị huy hiệu mới trong các API liên quan

## 5. Lưu ý

- Mỗi người dùng chỉ có một huy hiệu cao nhất cho mỗi nhóm
- Huy hiệu được cập nhật tự động dựa trên điểm hoạt động
- Thông tin huy hiệu được trả về trong các API liên quan đến người dùng và nhóm
