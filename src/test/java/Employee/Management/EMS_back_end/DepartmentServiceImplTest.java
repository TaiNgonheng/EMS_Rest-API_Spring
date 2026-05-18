package Employee.Management.EMS_back_end;

import Employee.Management.EMS_back_end.dto.DepartmentDto;
import Employee.Management.EMS_back_end.entity.Department;
import Employee.Management.EMS_back_end.exception.ResourceNotFoundException;
import Employee.Management.EMS_back_end.mapper.DepartmentMapper;
import Employee.Management.EMS_back_end.repository.DepartmentRepository;
import Employee.Management.EMS_back_end.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests — DepartmentServiceImpl
 *
 * What we test:
 *   - createDepartment(DepartmentDto)              → TC-UNIT-011, 012
 *   - getDepartmentById(Long)                      → TC-UNIT-013, 014
 *   - getAllDepartment()                            → TC-UNIT-015, 016
 *   - updateDepartment(Long, DepartmentDto)        → TC-UNIT-017, 018
 *   - deleteDepartment(Long)                       → TC-UNIT-019, 020
 *
 * Technique: Branch coverage, Mockito stubbing, MockedStatic for DepartmentMapper
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    // ── shared test data ──────────────────────────────
    private Department department;
    private DepartmentDto departmentDto;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setDepartmentName("Engineering");
        department.setDepartmentDescription("Handles all software development");

        departmentDto = new DepartmentDto();
        departmentDto.setId(1L);
        departmentDto.setDepartmentName("Engineering");
        departmentDto.setDepartmentDescription("Handles all software development");
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-011 | Happy Path | createDepartment
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-011: createDepartment with valid DTO — should save and return DepartmentDto")
    void createDepartment_validDto_shouldReturnSavedDto() {
        try (MockedStatic<DepartmentMapper> mapper = mockStatic(DepartmentMapper.class)) {
            mapper.when(() -> DepartmentMapper.mapToDepartment(departmentDto)).thenReturn(department);
            mapper.when(() -> DepartmentMapper.mapToDepartmentDto(department)).thenReturn(departmentDto);
            when(departmentRepository.save(department)).thenReturn(department);

            DepartmentDto result = departmentService.createDepartment(departmentDto);

            assertThat(result).isNotNull();
            assertThat(result.getDepartmentName()).isEqualTo("Engineering");
            assertThat(result.getDepartmentDescription()).isEqualTo("Handles all software development");
            verify(departmentRepository, times(1)).save(department);
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-012 | Sad Path | createDepartment — repo throws
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-012: createDepartment when repository fails — should propagate exception")
    void createDepartment_repositoryFails_shouldThrowException() {
        try (MockedStatic<DepartmentMapper> mapper = mockStatic(DepartmentMapper.class)) {
            mapper.when(() -> DepartmentMapper.mapToDepartment(departmentDto)).thenReturn(department);
            when(departmentRepository.save(department))
                    .thenThrow(new RuntimeException("DB connection failed"));

            assertThatThrownBy(() -> departmentService.createDepartment(departmentDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB connection failed");
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-013 | Happy Path | getDepartmentById
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-013: getDepartmentById with valid ID — should return correct DepartmentDto")
    void getDepartmentById_validId_shouldReturnDto() {
        try (MockedStatic<DepartmentMapper> mapper = mockStatic(DepartmentMapper.class)) {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            mapper.when(() -> DepartmentMapper.mapToDepartmentDto(department)).thenReturn(departmentDto);

            DepartmentDto result = departmentService.getDepartmentById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDepartmentName()).isEqualTo("Engineering");
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-014 | Sad Path | getDepartmentById — not found
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-014: getDepartmentById with non-existent ID — should throw ResourceNotFoundException")
    void getDepartmentById_notFound_shouldThrowResourceNotFoundException() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-015 | Happy Path | getAllDepartment
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-015: getAllDepartment — should return list of all DepartmentDtos")
    void getAllDepartment_shouldReturnMappedDtoList() {
        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setDepartmentName("Human Resources");
        dept2.setDepartmentDescription("Manages HR operations");

        DepartmentDto dto2 = new DepartmentDto();
        dto2.setId(2L);
        dto2.setDepartmentName("Human Resources");
        dto2.setDepartmentDescription("Manages HR operations");

        try (MockedStatic<DepartmentMapper> mapper = mockStatic(DepartmentMapper.class)) {
            when(departmentRepository.findAll()).thenReturn(List.of(department, dept2));
            mapper.when(() -> DepartmentMapper.mapToDepartmentDto(department)).thenReturn(departmentDto);
            mapper.when(() -> DepartmentMapper.mapToDepartmentDto(dept2)).thenReturn(dto2);

            List<DepartmentDto> result = departmentService.getAllDepartment();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getDepartmentName()).isEqualTo("Engineering");
            assertThat(result.get(1).getDepartmentName()).isEqualTo("Human Resources");
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-016 | Sad Path | getAllDepartment — empty list
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-016: getAllDepartment when no departments exist — should return empty list")
    void getAllDepartment_noDepartments_shouldReturnEmptyList() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        List<DepartmentDto> result = departmentService.getAllDepartment();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-017 | Happy Path | updateDepartment
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-017: updateDepartment with valid ID — should update fields and return updated DepartmentDto")
    void updateDepartment_validId_shouldReturnUpdatedDto() {
        DepartmentDto updateRequest = new DepartmentDto();
        updateRequest.setDepartmentName("Engineering & Design");
        updateRequest.setDepartmentDescription("Handles software and UI/UX design");

        Department updatedDepartment = new Department();
        updatedDepartment.setId(1L);
        updatedDepartment.setDepartmentName("Engineering & Design");
        updatedDepartment.setDepartmentDescription("Handles software and UI/UX design");

        DepartmentDto updatedDto = new DepartmentDto();
        updatedDto.setId(1L);
        updatedDto.setDepartmentName("Engineering & Design");
        updatedDto.setDepartmentDescription("Handles software and UI/UX design");

        try (MockedStatic<DepartmentMapper> mapper = mockStatic(DepartmentMapper.class)) {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);
            mapper.when(() -> DepartmentMapper.mapToDepartmentDto(updatedDepartment)).thenReturn(updatedDto);

            DepartmentDto result = departmentService.updateDepartment(1L, updateRequest);

            assertThat(result.getDepartmentName()).isEqualTo("Engineering & Design");
            assertThat(result.getDepartmentDescription()).isEqualTo("Handles software and UI/UX design");
            verify(departmentRepository, times(1)).save(any(Department.class));
        }
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-018 | Sad Path | updateDepartment — not found
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-018: updateDepartment with non-existent ID — should throw ResourceNotFoundException")
    void updateDepartment_notFound_shouldThrowResourceNotFoundException() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.updateDepartment(99L, departmentDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(departmentRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-019 | Happy Path | deleteDepartment
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-019: deleteDepartment with valid ID — should call deleteById once")
    void deleteDepartment_validId_shouldCallDeleteById() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        doNothing().when(departmentRepository).deleteById(1L);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).deleteById(1L);
    }

    // ─────────────────────────────────────────────────
    // TC-UNIT-020 | Sad Path | deleteDepartment — not found
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("TC-UNIT-020: deleteDepartment with non-existent ID — should throw ResourceNotFoundException")
    void deleteDepartment_notFound_shouldThrowResourceNotFoundException() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.deleteDepartment(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(departmentRepository, never()).deleteById(any());
    }
}
