package Employee.Management.EMS_back_end.service.impl;

import Employee.Management.EMS_back_end.dto.DepartmentDto;
import Employee.Management.EMS_back_end.entity.Department;
import Employee.Management.EMS_back_end.mapper.DepartmentMapper;
import Employee.Management.EMS_back_end.repository.DepartmentRepository;
import Employee.Management.EMS_back_end.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
