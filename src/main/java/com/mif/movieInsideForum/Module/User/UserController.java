package com.mif.movieInsideForum.Module.User;

import com.mif.movieInsideForum.Collection.Role;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.ProfilePostDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.DTO.UserUpdateDTO;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import com.mif.movieInsideForum.Module.Actor.ActorService;
import com.mif.movieInsideForum.Module.Post.service.GroupPostService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Year;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final GroupPostService groupPostService;
    private final AuthenticationFacade authenticationFacade;
    private final ActorService actorService;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("users/{userId}/posts")
    public ResponseEntity<ResponseWrapper<Slice<ProfilePostDTO>>> getProfilePostByUserId(
            @PathVariable ObjectId userId, 
            @PageableDefault(size = 5) Pageable pageable,
            Authentication authentication) {
        
        // Kiểm tra nếu người dùng hiện tại đang xem profile của chính họ
        boolean isCurrentUser = userId.equals(new ObjectId(authentication.getName()));
        
        // Nếu là profile của chính họ thì truyền true, không thì false
        boolean includeBlocked = isCurrentUser;
        
        Slice<ProfilePostDTO> posts = groupPostService.getProfilePost(userId, pageable, includeBlocked);
        return ResponseEntity.ok(ResponseWrapper.<Slice<ProfilePostDTO>>builder()
                .status("success")
                .message("Danh sách bài đăng của người dùng")
                .data(posts)
                .build());
    }

    @GetMapping("users/{userId}/info")
    public ResponseEntity<ResponseWrapper<User>> getUserInfo(@PathVariable ObjectId userId) {
        User user = userService.getUserInfo(userId);
        return ResponseEntity.ok(ResponseWrapper.<User>builder()
                .status("success")
                .message("Thông tin người dùng")
                .data(user)
                .build());
    }

    @PatchMapping("/my-profile")
    public ResponseEntity<ResponseWrapper<User>> updateUserProfile(@RequestBody UserUpdateDTO userUpdateDTO) throws IllegalAccessException {
        ObjectId userId = authenticationFacade.getUser().getId();
        userService.updateProfile(userId, userUpdateDTO);
        return ResponseEntity.ok(ResponseWrapper.<User>builder()
                .status("success")
                .message("Cập nhật thông tin người dùng thành công")
                .data(null)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper<Page<User>>> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        Page<User> users = userService.findAllAsPage(pageable);
        return ResponseEntity.ok(ResponseWrapper.<Page<User>>builder()
                .status("success")
                .message("Danh sách người dùng")
                .data(users)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ResponseWrapper<Void>> changeUserRole(@PathVariable ObjectId userId, @RequestParam Role newRole) {
        userService.changeUserRole(userId, newRole);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("User role updated successfully")
                .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ResponseWrapper<Void>> setAccountStatus(@PathVariable ObjectId userId, @RequestParam boolean isLocked) {
        userService.setAccountStatus(userId, isLocked);
        return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                .status("success")
                .message("User account status updated successfully")
                .build());
    }

    @GetMapping("/users/export")
    // not need authenticate
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> exportUsersToExcel() {
        try {
            List<User> users = userRepository.findAll();
            
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Users");
            
            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tên hiển thị", "Email", "Ngày sinh", "Loại tài khoản", "Số dư", "Ngày tạo"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }
            
            // Thêm dữ liệu
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId().toString());
                row.createCell(1).setCellValue(user.getDisplayName());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getDob() != null ? user.getDob().toString() : "");
                row.createCell(4).setCellValue(user.getUserType() != null ? user.getUserType().toString() : "");
                row.createCell(5).setCellValue(String.valueOf(user.getBalance()));
                row.createCell(6).setCellValue(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
            }
            
            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Tạo file Excel
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            // Tạo tên file với timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "users_" + timestamp + ".xlsx";
            
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/statistics/monthly")
    public ResponseEntity<ResponseWrapper<Map<Integer, Integer>>> getUserStatisticsByMonth(@RequestParam(value = "year", required = false) Integer year) {
        int queryYear = (year != null) ? year : Year.now().getValue();
        Map<Integer, Integer> stats = userService.countUsersByMonth(queryYear);
        return ResponseEntity.ok(ResponseWrapper.<Map<Integer, Integer>>builder()
                .status("success")
                .message("Thống kê số lượng người dùng theo từng tháng năm " + queryYear)
                .data(stats)
                .build());
    }

}