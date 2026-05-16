package Employee.Management.EMS_back_end;

import Employee.Management.EMS_back_end.dto.EmployeeDto;
import Employee.Management.EMS_back_end.entity.Employee;
import Employee.Management.EMS_back_end.exception.ResourceNotFoundException;
import Employee.Management.EMS_back_end.repository.EmployeeRepository;
import Employee.Management.EMS_back_end.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import Employee.Management.EMS_back_end.mapper.EmployeeMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests — EmployeeServiceImpl
 *
 * What we test:
 *   - createEmployee(EmployeeDto)         → TC-UNIT-001, 002
 *   - getEmployeeById(Long)               → TC-UNIT-003, 004
 *   - getAllEmployees()                    → TC-UNIT-005
 *   - updateEmployee(Long, Employee)      → TC-UNIT-006, 007
 *   - deleteEmployee(Long)                → TC-UNIT-008, 009, 010
 *
 * Technique: Branch coverage, Mockito stubbing, MockedStatic for EmployeeMapper
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // ── shared test data ──────────────────────────────
    private Employee employee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstname("Dara");
        employee.setLastname("Sok");
        employee.setEmail("dara.sok@ems.com");

        employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setFirstname("Dara");
        employeeDto.setLastname("Sok");
        employeeDto.setEmail("dara.sok@ems.com");
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-001 | Happy Path | createEmployee
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-001: createEmployee with valid DTO — should save and return EmployeeDto")
    void createEmployee_validDto_shouldReturnSavedDto() {
        try (MockedStatic<EmployeeMapper> mapper = mockStatic(EmployeeMapper.class)) {
            mapper.when(() -> EmployeeMapper.mapToEmployee(employeeDto)).thenReturn(employee);
            mapper.when(() -> EmployeeMapper.mapToEmployeeDto(employee)).thenReturn(employeeDto);
            when(employeeRepository.save(employee)).thenReturn(employee);

            EmployeeDto result = employeeService.createEmployee(employeeDto);

            assertThat(result).isNotNull();
            assertThat(result.getFirstname()).isEqualTo("Dara");
            assertThat(result.getLastname()).isEqualTo("Sok");
            assertThat(result.getEmail()).isEqualTo("dara.sok@ems.com");
            verify(employeeRepository, times(1)).save(employee);
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-002 | Sad Path | createEmployee — repo throws
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-002: createEmployee when repository fails — should propagate exception")
    void createEmployee_repositoryFails_shouldThrowException() {
        try (MockedStatic<EmployeeMapper> mapper = mockStatic(EmployeeMapper.class)) {
            mapper.when(() -> EmployeeMapper.mapToEmployee(employeeDto)).thenReturn(employee);
            when(employeeRepository.save(employee))
                    .thenThrow(new RuntimeException("DB connection failed"));

            assertThatThrownBy(() -> employeeService.createEmployee(employeeDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB connection failed");
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-003 | Happy Path | getEmployeeById
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-003: getEmployeeById with valid ID — should return correct EmployeeDto")
    void getEmployeeById_validId_shouldReturnDto() {
        try (MockedStatic<EmployeeMapper> mapper = mockStatic(EmployeeMapper.class)) {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            mapper.when(() -> EmployeeMapper.mapToEmployeeDto(employee)).thenReturn(employeeDto);

            EmployeeDto result = employeeService.getEmployeeById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("dara.sok@ems.com");
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-004 | Sad Path | getEmployeeById — not found
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-004: getEmployeeById with non-existent ID — should throw ResourceNotFoundException")
    void getEmployeeById_notFound_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-005 | Happy Path | getAllEmployees
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-005: getAllEmployees — should return list of all EmployeeDtos")
    void getAllEmployees_shouldReturnMappedDtoList() {
        Employee emp2 = new Employee();
        emp2.setId(2L);
        emp2.setFirstname("Sophea");
        emp2.setLastname("Chan");
        emp2.setEmail("sophea.chan@ems.com");

        EmployeeDto dto2 = new EmployeeDto();
        dto2.setId(2L);
        dto2.setFirstname("Sophea");
        dto2.setLastname("Chan");
        dto2.setEmail("sophea.chan@ems.com");

        try (MockedStatic<EmployeeMapper> mapper = mockStatic(EmployeeMapper.class)) {
            when(employeeRepository.findAll()).thenReturn(List.of(employee, emp2));
            mapper.when(() -> EmployeeMapper.mapToEmployeeDto(employee)).thenReturn(employeeDto);
            mapper.when(() -> EmployeeMapper.mapToEmployeeDto(emp2)).thenReturn(dto2);

            List<EmployeeDto> result = employeeService.getAllEmployees();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getFirstname()).isEqualTo("Dara");
            assertThat(result.get(1).getFirstname()).isEqualTo("Sophea");
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-006 | Happy Path | updateEmployee
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-006: updateEmployee with valid ID — should update fields and return updated EmployeeDto")
    void updateEmployee_validId_shouldReturnUpdatedDto() {
        Employee updateRequest = new Employee();
        updateRequest.setFirstname("Dara");
        updateRequest.setLastname("Chan");
        updateRequest.setEmail("dara.chan@ems.com");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setFirstname("Dara");
        updatedEmployee.setLastname("Chan");
        updatedEmployee.setEmail("dara.chan@ems.com");

        EmployeeDto updatedDto = new EmployeeDto();
        updatedDto.setId(1L);
        updatedDto.setFirstname("Dara");
        updatedDto.setLastname("Chan");
        updatedDto.setEmail("dara.chan@ems.com");

        try (MockedStatic<EmployeeMapper> mapper = mockStatic(EmployeeMapper.class)) {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
            mapper.when(() -> EmployeeMapper.mapToEmployeeDto(updatedEmployee)).thenReturn(updatedDto);

            EmployeeDto result = employeeService.updateEmployee(1L, updateRequest);

            assertThat(result.getLastname()).isEqualTo("Chan");
            assertThat(result.getEmail()).isEqualTo("dara.chan@ems.com");
            verify(employeeRepository, times(1)).save(any(Employee.class));
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-007 | Sad Path | updateEmployee — not found
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-007: updateEmployee with non-existent ID — should throw ResourceNotFoundException")
    void updateEmployee_notFound_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(99L, employee))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(employeeRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-008 | Happy Path | deleteEmployee
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-008: deleteEmployee with valid ID — should call deleteById once")
    void deleteEmployee_validId_shouldCallDeleteById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-009 | Sad Path | deleteEmployee — not found
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-009: deleteEmployee with non-existent ID — should throw ResourceNotFoundException")
    void deleteEmployee_notFound_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(employeeRepository, never()).deleteById(any());
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-010 | Sad Path | getAllEmployees — empty list
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-010: getAllEmployees when no employees exist — should return empty list")
    void getAllEmployees_noEmployees_shouldReturnEmptyList() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}
