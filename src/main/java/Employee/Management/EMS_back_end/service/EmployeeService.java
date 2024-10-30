package Employee.Management.EMS_back_end.service;

import Employee.Management.EMS_back_end.dto.EmployeeDto;
import Employee.Management.EMS_back_end.entity.Employee;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long employeeId);

    List<EmployeeDto> getAllEmployees();

    EmployeeDto updateEmployee(Long employeeId, Employee updateEmployee);

    void deleteEmployee(Long employeeId);
}
