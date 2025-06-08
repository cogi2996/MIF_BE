package com.mif.movieInsideForum.Module.Ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test/vector")
public class VectorStoreTestService {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreTestService.class);

    @Autowired
    private VectorStore vectorStore;

    @GetMapping("/add-and-search")
    public Map<String, Object> testVectorStore() {
        try {
            log.info("Bắt đầu test VectorStore");

            // Tạo documents test
            List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", 
                    Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", 
                    Map.of("meta2", "meta2"))
            );

            // Thêm documents vào VectorStore
            log.info("Thêm {} documents vào VectorStore", documents.size());
            vectorStore.add(documents);

            // Tìm kiếm theo "Spring"
            log.info("Tìm kiếm documents với query 'Spring'");
            List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query("Spring").topK(5).build()
            );
            
            // Log kết quả tìm kiếm
            log.info("Tìm thấy {} documents với query 'Spring'", results.size());
            results.forEach(doc -> 
                log.info("Document ID: {}, Text: {}", 
                    doc.getId(), 
                    doc.getText())
            );

            // Trả về kết quả
            return Map.of(
                "success", true,
                "documentsAdded", documents.size(),
                "resultsFound", results.size()
            );
            
        } catch (Exception e) {
            log.error("Lỗi khi test VectorStore: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
} 