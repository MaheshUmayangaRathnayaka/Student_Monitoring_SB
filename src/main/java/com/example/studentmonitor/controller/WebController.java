package com.example.studentmonitor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.studentmonitor.dto.PerformanceDTO;
import com.example.studentmonitor.dto.StudentDTO;
import com.example.studentmonitor.service.PerformanceService;
import com.example.studentmonitor.service.StudentService;

import jakarta.validation.Valid;

@Controller
public class WebController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private PerformanceService performanceService;
    
    @Value("${spring.profiles.active:development}")
    private String activeProfile;
    
    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    // Add development mode flag to all views
    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("isDevelopmentMode", "development".equals(activeProfile));
        model.addAttribute("h2ConsoleEnabled", h2ConsoleEnabled);
    }

    @GetMapping("/")
    public String home(Model model) {
        List<StudentDTO> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("totalStudents", students.size());
        return "index";
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        List<StudentDTO> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "students/list";
    }

    @GetMapping("/students/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "students/form";
    }

    @PostMapping("/students")
    public String createStudent(@Valid @ModelAttribute StudentDTO student, 
                              RedirectAttributes redirectAttributes) {
        try {
            studentService.createStudent(student);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Student created successfully!");
            return "redirect:/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error creating student: " + e.getMessage());
            return "redirect:/students/new";
        }
    }

    @GetMapping("/students/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        try {
            StudentDTO student = studentService.getStudent(id);
            List<PerformanceDTO> performances = performanceService.getByStudent(id);
            
            model.addAttribute("student", student);
            model.addAttribute("performances", performances);
            
            // Calculate average score
            double averageScore = performances.stream()
                .mapToDouble(PerformanceDTO::getScore)
                .average()
                .orElse(0.0);
            model.addAttribute("averageScore", Math.round(averageScore * 100.0) / 100.0);
            
            return "students/detail";
        } catch (Exception e) {
            return "redirect:/students?error=Student not found";
        }
    }

    @GetMapping("/students/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            StudentDTO student = studentService.getStudent(id);
            model.addAttribute("student", student);
            return "students/form";
        } catch (Exception e) {
            return "redirect:/students?error=Student not found";
        }
    }

    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable Long id, 
                              @Valid @ModelAttribute StudentDTO student,
                              RedirectAttributes redirectAttributes) {
        try {
            studentService.updateStudent(id, student);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Student updated successfully!");
            return "redirect:/students/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating student: " + e.getMessage());
            return "redirect:/students/" + id + "/edit";
        }
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id, 
                              RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Student deleted successfully!");
            return "redirect:/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting student: " + e.getMessage());
            return "redirect:/students";
        }
    }

    @GetMapping("/students/{id}/delete")
    public String redirectDeleteStudent(@PathVariable Long id, 
                                      RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", 
            "Invalid delete request. Please use the delete button on the students page.");
        return "redirect:/students";
    }

    @GetMapping("/students/{studentId}/performances/new")
    public String showAddPerformanceForm(@PathVariable Long studentId, Model model) {
        try {
            StudentDTO student = studentService.getStudent(studentId);
            PerformanceDTO performance = new PerformanceDTO();
            
            model.addAttribute("student", student);
            model.addAttribute("performance", performance);
            return "performances/form";
        } catch (Exception e) {
            return "redirect:/students?error=Student not found";
        }
    }

    @PostMapping("/students/{studentId}/performances")
    public String addPerformance(@PathVariable Long studentId,
                               @Valid @ModelAttribute PerformanceDTO performance,
                               RedirectAttributes redirectAttributes) {
        try {
            performanceService.createPerformance(studentId, performance);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Performance record added successfully!");
            return "redirect:/students/" + studentId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error adding performance: " + e.getMessage());
            return "redirect:/students/" + studentId + "/performances/new";
        }
    }

    @PostMapping("/performances/{id}/delete")
    public String deletePerformance(@PathVariable Long id, 
                                  @RequestParam Long studentId,
                                  RedirectAttributes redirectAttributes) {
        try {
            performanceService.deletePerformance(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Performance record deleted successfully!");
            return "redirect:/students/" + studentId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting performance: " + e.getMessage());
            return "redirect:/students/" + studentId;
        }
    }
}