<li class="list-unstyled" style="list-style:none;padding-left:0;">
    <div class="treeview hover p-l-10">
        <input type="checkbox" id="${populationCriteria.id}" />
        <label for="${populationCriteria.id}" class="list-header"><b>${populationCriteria.name}</b></label>
        <#if populationCriteria.populations?size != 0>
            <ul>
                <#list populationCriteria.populations as population>
                    <#include "population.ftl">
                </#list>
            </ul>
        </#if>
    </div>
</li>