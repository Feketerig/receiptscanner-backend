package hu.levente.fazekas.receiptscannerbackend.controller

import hu.levente.fazekas.receiptscannerbackend.filestorage.FileStorageService
import hu.levente.fazekas.receiptscannerbackend.model.dto.request.ReceiptRequest
import hu.levente.fazekas.receiptscannerbackend.model.dto.response.ReceiptResponse
import hu.levente.fazekas.receiptscannerbackend.service.ReceiptService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/receipts")
class ReceiptController @Autowired constructor(
    val fileStorageService: FileStorageService,
    val receiptService: ReceiptService
) {


    @GetMapping
    fun getAll(
        @RequestParam("pageNumber") pageNumber: Int = 0,
        @RequestParam("pageSize") pageSize: Int = 20
    ): ResponseEntity<List<ReceiptResponse>> {
        return ResponseEntity(receiptService.getAllReceipts(pageNumber, pageSize), HttpStatus.OK)
    }

    @GetMapping("/version/{version}")
    fun getLatestVersion(
        @PathVariable("version") version: Long,
        @RequestParam("pageNumber") pageNumber: Int = 0,
        @RequestParam("pageSize") pageSize: Int = 20
    ): ResponseEntity<List<ReceiptResponse>>{
        val newReceipts = receiptService.getAllNewReceipts(version, pageNumber, pageSize)
        return ResponseEntity(newReceipts, HttpStatus.OK)
    }

    @PostMapping
    fun create(@RequestBody receiptRequest: List<ReceiptRequest>): ResponseEntity<Any>{
        receiptService.saveReceipts(receiptRequest)
        return ResponseEntity(null, HttpStatus.CREATED)
    }

    @PostMapping("/{id}/uploadFile")
    fun createFile(
        @RequestParam("file") file: MultipartFile,
        @PathVariable("id") clientId: Long
        ): ResponseEntity<Any> {
        val imageUri = fileStorageService.saveFile(file)
        receiptService.updateImageUrl(clientId, imageUri)
        return ResponseEntity(null, HttpStatus.CREATED)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") clientId: Long): ResponseEntity<Any>{
        receiptService.deleteReceipt(clientId)
        return ResponseEntity(null, HttpStatus.NO_CONTENT)
    }

    @PutMapping
    fun update(@RequestBody receiptRequest: ReceiptRequest): ResponseEntity<Any>{
        receiptService.updateReceipt(receiptRequest)
        return ResponseEntity(null, HttpStatus.NO_CONTENT)
    }

    @GetMapping("/{fileName:.+}")
    fun getFile(
        @PathVariable fileName: String,
        request: HttpServletRequest
    ): ResponseEntity<Resource> {
        val resource: Resource = fileStorageService.loadAsResources(fileName)

        val contentType = try {
            request.servletContext.getMimeType(resource.file.absolutePath)
        } catch (ex: IOException) {
            "application/octet-stream"
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.filename + "\"")
            .body(resource)
    }
}