package Employee.Management.EMS_back_end.service.impl;

import Employee.Management.EMS_back_end.dto.EmployeeDto;
import Employee.Management.EMS_back_end.entity.Employee;
import Employee.Management.EMS_back_end.exception.ResourceNotFoundException;
import Employee.Management.EMS_back_end.mapper.EmployeeMapper;
import Employee.Management.EMS_back_end.repository.EmployeeRepository;
import Employee.Management.EMS_back_end.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Employee savedEmployee= employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
       Employee employee = employeeRepository.
               findById(employeeId).orElseThrow(() ->
                       new ResourceNotFoundException("Employee is not exists with given id: "+employeeId));
        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees= employeeRepository.findAll();
        return employees.stream().map((employee) -> EmployeeMapper.mapToEmployeeDto(employee)).collect(Collectors.toList());
    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, Employee updateEmployee) {

       Employee employee = employeeRepository.findById(employeeId).
                orElseThrow(() -> new ResourceNotFoundException("Exception not exists "+employeeId));
        employee.setFirstname(updateEmployee.getFirstname());
        employee.setLastname(updateEmployee.getLastname());
        employee.setEmail(updateEmployee.getEmail());

        Employee updateEmployeeObj =employeeRepository.save(employee);
        
        return EmployeeMapper.mapToEmployeeDto(updateEmployeeObj);
    }


    @Override
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).
                orElseThrow(() -> new ResourceNotFoundException
                        ("Exception not exists "+employeeId));
        employeeRepository.deleteById(employeeId);
    }
}
