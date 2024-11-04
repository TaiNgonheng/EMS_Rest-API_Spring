package Employee.Management.EMS_back_end.mapper;

import Employee.Management.EMS_back_end.dto.DepartmentDto;
import Employee.Management.EMS_back_end.entity.Department;

public class DepartmentMapper {
    //convert department jpa entity to departmment dto
    public static DepartmentDto mapToDepartmentDto(Department department){
        return new DepartmentDto(
                department.getId(),
                department.getDepartmentName(),
                department.getDepartmentDescription()
        );
    }
    // convert department dto into department jpa entity
    public static Department mapToDepartment(DepartmentDto departmentDto){
        return new Department(
                departmentDto.getId(),
                departmentDto.getDepartmentName(),
                departmentDto.getDepartmentDescription()
        );
    }
}
