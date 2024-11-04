package Employee.Management.EMS_back_end.service.impl;

import Employee.Management.EMS_back_end.dto.DepartmentDto;
import Employee.Management.EMS_back_end.entity.Department;
import Employee.Management.EMS_back_end.exception.ResourceNotFoundException;
import Employee.Management.EMS_back_end.mapper.DepartmentMapper;
import Employee.Management.EMS_back_end.repository.DepartmentRepository;
import Employee.Management.EMS_back_end.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private DepartmentRepository departmentRepository;
    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {

        Department department = DepartmentMapper.mapToDepartment(departmentDto);
        Department savedDepartment = departmentRepository.save(department);

        return DepartmentMapper.mapToDepartmentDto(savedDepartment);
    }

    @Override
    public DepartmentDto getDepartmentById(Long departmentId) {

        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new ResourceNotFoundException("Department is not exists with given id: "+departmentId)
        );
        return DepartmentMapper.mapToDepartmentDto(department);
    }

    @Override
    public List<DepartmentDto> getAllDepartment() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream().map((department)-> DepartmentMapper.mapToDepartmentDto(department)).collect(Collectors.toList());
    }

    @Override
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto updateDepartment) {
        Department department = departmentRepository.findById(departmentId).
                orElseThrow(()-> new ResourceNotFoundException("department not exists" +departmentId));
        department.setDepartmentName(updateDepartment.getDepartmentName());
        department.setDepartmentDescription(updateDepartment.getDepartmentDescription());

        Department updateDepartmentObj = departmentRepository.save(department);
        return DepartmentMapper.mapToDepartmentDto(updateDepartmentObj);
    }

    @Override
    public void deleteDepartment(Long departmentId) {
    Department department = departmentRepository.findById(departmentId).
            orElseThrow(()-> new ResourceNotFoundException
                    ("Id for delete does not exist :"+ departmentId));
    departmentRepository.deleteById(departmentId);
    }
}
