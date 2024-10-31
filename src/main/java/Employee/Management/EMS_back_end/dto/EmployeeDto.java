package Employee.Management.EMS_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto  {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
}
