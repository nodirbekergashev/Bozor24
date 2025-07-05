package uz.pdp.wrapperLists;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pdp.model.Category;
//import uz.pdp.model.Category;

import java.util.List;

@JacksonXmlRootElement(localName = "Categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryListWrapper {
     @JacksonXmlProperty(localName = "category")
    @JacksonXmlElementWrapper(useWrapping = false)
     private List<Category> categoryList;
}
