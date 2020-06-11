package mat.server.service.cql;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mat.shared.CQLError;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "errors")
public class LibraryErrors {

    @NotBlank
    private String name;
    @NotBlank
    private String version;
    private List<CQLError> errors = new ArrayList<>();

    public LibraryErrors(@NotBlank String name, @NotBlank String version) {
        this.name = name;
        this.version = version;
    }

}
