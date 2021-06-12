package com.fjs.api2dextra.controller;

import java.util.ArrayList;
import java.util.List;

import com.fjs.api2dextra.dto.StudentRq;
import com.fjs.api2dextra.dto.StudentRs;
import com.fjs.api2dextra.model.Student;
import com.fjs.api2dextra.services.HouseService;
import com.fjs.api2dextra.services.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/characters")
@CrossOrigin(origins = "*")
@Api(value = "Characters", description = "API REST para os personagens", tags = {"Characters"})
public class Character {

    @Autowired
    private StudentService studentService;

    @Autowired
    private HouseService houseService;

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna uma lista com todos os personagens."),
            @ApiResponse(code = 403, message = "Você não tem permissão."),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção.") })
    @GetMapping(path = "/", produces = "application/json")
    @ApiOperation(value = "Retorna todos os personagens cadastrados.", tags = {"Characters"})
    public ResponseEntity<List<StudentRs>> getAll() {
        try {
            List<StudentRs> students = new ArrayList<StudentRs>();
            /** obter todos os personagens */
            studentService.findAllStudentRs().forEach(students::add);

            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna um personagen."),
            @ApiResponse(code = 403, message = "Você não tem permissão."),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção.") })
    @GetMapping(path = "/{id}", produces = "application/json")
    @ApiOperation(value = "Retorna um personagen filtrando pelo id.", tags = {"Characters"})
    public ResponseEntity<StudentRs> getCharacter(@PathVariable("id") Integer id) {
        try {
            StudentRs student = new StudentRs();

            /** obter personagem filtrando pelo id */
            student = StudentRs.converter(studentService.findById(id));

            /** statusCode 404 informando que não encontrou o registro */
            if (student == null)
                return ResponseEntity.noContent().build();

            return ResponseEntity.ok(student);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna uma lista com todos os personagens filtrados pelo id da casa."),
            @ApiResponse(code = 403, message = "Você não tem permissão."),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção.") })
    @GetMapping(path = "/house/{id}", produces = "application/json")
    @ApiOperation(value = "Retorna todos os personagens cadastrados filtrando por sua respectiva casa.", tags = {"Characters"})
    public ResponseEntity<List<StudentRs>> getCharactersByHouse(@PathVariable("id") String id) {
        try {
            List<StudentRs> students = new ArrayList<StudentRs>();

            /** obter personagem por sua respectiva casa */
            students = studentService.findCharactersByHouse(id);

            if (students.size() == 0)
                return ResponseEntity.noContent().build();

            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Cadastra um personagem nos registros."),
            @ApiResponse(code = 403, message = "Você não tem permissão."),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção.") })
    @RequestMapping(path = "/", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    @ApiOperation(value = "Cadastro de personagens.", tags = {"Characters"})
    public ResponseEntity<Void> save(@RequestBody StudentRq studentRq) throws Exception {
        try {
            studentService.save(studentRq);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Exclui um personagem dos registros."),
            @ApiResponse(code = 403, message = "Você não tem permissão."),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção.") })
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Exclusão de personagens.", tags = {"Characters"})
    public ResponseEntity<Boolean> delete(@PathVariable("id") Integer id) {
        boolean hasDelected = false;
        try {
            Student student = new Student();

            /** obter personagem filtrando pelo id */
            student = studentService.findById(id);

            /** exclui o personagem */
            hasDelected = studentService.delete(student);

        } catch (Exception e) {

        }

        if (hasDelected)/** statusCode = 204 para exclusão sem retorno no response */
            return ResponseEntity.noContent().build();
        else /** statusCode = 404 para informar que não encontrou o registro para exclusão */
            return ResponseEntity.notFound().build();

    }

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Atualiza o registro do personagem."),
            @ApiResponse(code = 403, message = "Você não tem permissão."),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção.") })
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Atualização de personagens.", tags = {"Characters"})
    public ResponseEntity<Boolean> update(@PathVariable("id") Integer id, @RequestBody StudentRq student) {
        Student st = new Student();
        try {
            st = studentService.findById(id);  
            st.setName(student.getName());          
            st.setRole(student.getRole());
            st.setPatronus(student.getPatronus());
            st.setHouse(houseService.findById(student.getHouse()));                        
            studentService.update(st);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }
}