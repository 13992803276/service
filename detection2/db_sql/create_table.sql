use detection2;
create table traffic
(
    id      int auto_increment primary key,
    root_no varchar(255) null,
    year    long,
    xxhc    long,
    zxhc    long,
    dxhc    long,
    tdhc    long,
    jzxc    long,
    zxkc    long,
    dkc     long
) charset = utf8;

select *
from (
         select t.year,
                '交通量' as type,
                t.root_no,
                xxhc,
                zxhc,
                dxhc,
                tdhc,
                jzxc,
                zxkc,
                dkc
               -- xxhc+zxhc+dxhc+tdhc+jzxc+zxkc+dkc as total
         from traffic t
         union all
         select t2.year,
                '增长率'                                                      as type,
                t2.root_no,
                concat(round((t2.xxhc - t1.xxhc) / t1.xxhc * 100, 2), '%') as xxhc,
                concat(round((t2.zxhc - t1.zxhc) / t1.zxhc * 100, 2), '%') as zxhc,
                concat(round((t2.dxhc - t1.dxhc) / t1.dxhc * 100, 2), '%') as dxhc,
                concat(round((t2.tdhc - t1.tdhc) / t1.tdhc * 100, 2), '%') as tdhc,
                concat(round((t2.jzxc - t1.jzxc) / t1.jzxc * 100, 2), '%') as jzxc,
                concat(round((t2.zxkc - t1.zxkc) / t1.zxkc * 100, 2), '%') as zxkc,
                concat(round((t2.dkc - t1.dkc) / t1.dkc * 100, 2), '%')    as dkc
         from traffic t1
                  inner join traffic t2
                             on t1.root_no = t2.root_no
                                 and t1.year + 1 = t2.year
         where t2.year > 2016
     ) p
where p.root_no = 'G330'
order by p.year