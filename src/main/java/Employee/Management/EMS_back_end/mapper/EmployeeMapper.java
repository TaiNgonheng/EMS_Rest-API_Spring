package Employee.Management.EMS_back_end.mapper;

//import Employee.Management.EMS_back_end.dto.EmployeeDto;
import Employee.Management.EMS_back_end.dto.EmployeeDto;
import Employee.Management.EMS_back_end.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee){
        return new EmployeeDto(
            employee.getId(),
            employee.getFirstname(),
            employee.getLastname(),
            employee.getEmail()
        );
    }

    public static Employee mapToEmployee(EmployeeDto employeeDto){
        return new Employee(
                employeeDto.getId(),
                employeeDto.getFirstname(),
                employeeDto.getLastname(),
                employeeDto.getEmail()
        );
    }
}
