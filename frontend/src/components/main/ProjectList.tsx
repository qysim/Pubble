// 1. react 관련
import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom";
// 2. library 관련
import { getProject } from "@/apis/project";
// 3. api 관련
import  ProjectAddModal  from "@/components/main/ProjectAddModal"
// 4. store 관련
import usePageInfoStore from "@/stores/pageInfoStore";
// 5. component 관련
import { Cell, Header, HeaderGroup, Row, ColumnDef, ColumnFiltersState, SortingState, VisibilityState, flexRender, getCoreRowModel, getFilteredRowModel, getPaginationRowModel, getSortedRowModel, useReactTable } from "@tanstack/react-table"
// import { MoreHorizontal } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
// import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"


// Project type 정의
export type Project = {
  projectId: number // 프로젝트 pk
  prdId: string // 프로젝트 code
  startAt: string // 프로젝트 시작일
  endAt: string // 프로젝트 종료일
  projectTitle: string // 프로젝트 이름
  people: number // 프로젝트 참여자의 수(실수)
  progressRatio: number // 프로젝트 진행률(실수)
  status: "in progress" | "complete" | "before start" // 프로젝트 상태
}
// column 정의
export const columns: ColumnDef<Project>[] = [
  {
    // 선택된 행의 체크박스
    id: "select",
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && "indeterminate")
        }
        onCheckedChange={(value: boolean) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value: boolean) => row.toggleSelected(!!value)}
        aria-label="Select row"
      />
    ),
    enableSorting: false,
    enableHiding: false,
  }, 
  {
    accessorKey: "prdId",
    header: "프로젝트 코드",
    cell: ({ row }) => <div>{row.getValue("prdId")}</div>,
  },
  {
    accessorKey: "projectTitle",
    header: "프로젝트 이름",
    cell: ({ row }) => <div>{row.getValue("projectTitle")}</div>,
  },
  {
    accessorKey: "people",
    header: "구성원 수",
    cell: ({ row }) => <div>{(row.getValue("people") as string[]).length}</div>,
  },
  {
    accessorKey: "progressRatio",
    header: "진행률",
    cell: ({ row }) => <div>{row.getValue("progressRatio")}</div>,
  },
  {
    accessorKey: "status",
    header: "상태",
    cell: ({ row }) => <div>{row.getValue("status")}</div>,
  },
  {
    accessorKey: "startAt",
    header: "시작일",
    cell: ({ row }:{ row: Row<Project> }) => (
      <div className="capitalize">{(row.getValue("startAt") as string).split('T')[0]}</div>
    ),
  },
  {
    accessorKey: "endAt",
    header: "종료일",
    cell: ({ row }:{ row: Row<Project> }) => (
      <div className="capitalize">{(row.getValue("endAt") as string).split('T')[0]}</div>
    ),
  },

  // {
  //   id: "actions",
  //   enableHiding: false,
  //   cell: ({ row }:{ row: Row<Project> }) => {

  //     return (
  //       <DropdownMenu>
  //         <DropdownMenuTrigger asChild>
  //           <Button variant="ghost" className="h-8 w-8 p-0">
  //             <span className="sr-only">Open menu</span>
  //             <MoreHorizontal className="h-4 w-4" />
  //           </Button>
  //         </DropdownMenuTrigger>
  //         <DropdownMenuContent align="end">
  //           <DropdownMenuLabel>부가기능</DropdownMenuLabel>
  //             <DropdownMenuItem>프로젝트 현황 확인</DropdownMenuItem>
  //         </DropdownMenuContent>
  //       </DropdownMenu>
  //     )
  //   },
  // },
]
// ProjectList: 특정 사용자의 프로젝트를 개괄적으로 보는 컴포넌트
const ProjectList = () => {
  const { setPageType } = usePageInfoStore();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();
  
  // useState를 통한 상태변화 관리 들어가기
  // 프로젝트의 목록
  const [projects, setProjects] = useState<Project[]>([]);
  // 정렬 상태
  const [sorting, setSorting] = useState<SortingState>([]);
  // column 필터링 상태
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  // column 보이기 상태
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  // row 선택 상태
  const [rowSelection, setRowSelection] = useState({});
  // table 상태
  const table = useReactTable({
    data: projects,
    columns,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection,
    },
  })
  // useEffect를 활용한... 사용자의 프로젝트 목록 화면 UI 갱신
  useEffect(() => {
    // fetchProjects 함수 정의
    const fetchProjects = async () => {
      // 프로젝트 목록 조회 API 호출
      try { const response = await getProject();
      // 프로젝트 목록 데이터 추출
      if (response.data && response.data.length > 0) {
        const projectData = response.data.map((pjt: Project) => ({
          projectId: pjt.projectId,
          prdId: pjt.prdId,
          startAt: pjt.startAt,
          endAt: pjt.endAt,
          projectTitle: pjt.projectTitle,
          people: pjt.people,
          progressRatio: pjt.progressRatio,
          status: pjt.status,
        }));
        // 현재 컴포넌트에서 사용하는 '프로젝트 목록 배열'(=Project[])의 상태 업데이트 시행
        setProjects(projectData);

      // 데이터가 없는 경우
      } else {
        // 빈 배열로 프로젝트 목록 데이터 설정
        setProjects([]);
      }
      // 에러 발생 시
    } catch (error) {
      console.error("Error fetching projects:", error);
      // 빈 배열로 프로젝트 목록 데이터 설정
      setProjects([]);
    }
  };
  // 프로젝트 목록 조회 API 호출
  fetchProjects();
  }, []); // 빈 배열로 설정하여 컴포넌트 처음 렌더링 시에만 실행
  

  // 새로운 프로젝트 생성 관련.
  // 사용자의 프로젝트 목록에 새로운 프로젝트 추가하기. // 프로젝트 생성 모달 true. 
  const handleAddProject = () => {
    // 프로젝트 생성 모달 true.
    setIsModalOpen(true);
  };
  // 프로젝트 생성 모달 닫기.
  const handleCloseModal = () => {
    // 프로젝트 생성 모달 false.
    setIsModalOpen(false);
  };

  // 특정 프로젝트 row 클릭시, 특정 프로젝트에 진입할 수 있도록 하는 함수.
  const handleRowClick = (row: Row<Project>) => {
    const { prdId, projectId, projectTitle } = row.original;
    const pId = projectId;
    const pCode = prdId;
    const pName = projectTitle;
    // 프로젝트 상세 정보 페이지로 이동
    console.log("Clicked project ID:", pId);
    if (pCode && pName) {
      // 프로젝트 상세 정보 페이지로 이동
      // ps1.기존에는 state로 정보를 다음 페이지에 넘겼는데, 이제 모두 store 사용하므로 state 파라미터는 사용하지 않음
      setPageType('project', {
        projectId: pId,
        projectCode: pCode,
        projectName: pName,
      });
      navigate(`/project/${pCode}`);
    } else {
      console.error("Missing prdId or projectTitle in the data");
    }
  };

  return (
    
    <div className="w-full px-2 ">
      {/* 프로젝트 생성 모달 */}
      <ProjectAddModal isOpen={isModalOpen} onClose={handleCloseModal} />
      {/* 프로젝트 추가 버튼 */}
      <Button onClick={handleAddProject}>프로젝트 추가</Button>
      <div className="flex items-center py-2">
        {/* 프로젝트 이름 검색 입력창 */}
        <Input
          placeholder="프로젝트 이름을 입력해주요."
          value={(table.getColumn("projectTitle")?.getFilterValue() as string) ?? ""}
          onChange={(event) =>
            table.getColumn("projectTitle")?.setFilterValue(event.target.value)
          }
          className="max-w-sm"
        />

      </div>
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup: HeaderGroup<Project>) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header: Header<Project, unknown>) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                    </TableHead>
                  )
                })}
              </TableRow>
            ))}
          </TableHeader>
          {/* 테이블 몸체 */}
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row: Row<Project> ) => (
                // 테이블 행
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && "selected"}
                  onClick={() => handleRowClick(row)}
                >
                  {row.getVisibleCells().map((cell: Cell<Project, unknown>) => (
                    // 테이블 셀
                    <TableCell 
                      key={cell.id}
                      >
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
                // 테이블 행 끝
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-14 text-center"
                >
                  프로젝트가 없습니다.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex items-center justify-end space-x-2 py-1">
        <div className="flex-1 text-sm text-muted-foreground">
          {table.getFilteredSelectedRowModel().rows.length} of{" "}
          {table.getFilteredRowModel().rows.length} row(s) selected.
        </div>
        <div className="space-x-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => table.previousPage()}
            disabled={!table.getCanPreviousPage()}
          >
            이전
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => table.nextPage()}
            disabled={!table.getCanNextPage()}
          >
            다음
          </Button>
        </div>
      </div>
    </div>

  )
}

export default ProjectList;